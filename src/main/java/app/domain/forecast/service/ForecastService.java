package app.domain.forecast.service;

import app.commonUtil.apiPayload.ApiResponse;
import app.domain.forecast.client.FastApiClient;

import app.domain.forecast.client.StoreInternalApiClient;
import app.domain.forecast.document.ForecastDocument;
import app.domain.forecast.model.dto.request.FastApiRequest;
import app.domain.forecast.model.dto.request.GetForecastRequest;
import app.domain.forecast.model.dto.request.RealDataItem;
import app.domain.forecast.model.dto.response.FastApiResponse;
import app.domain.forecast.model.dto.response.GetForecastResponse;
import app.domain.forecast.model.dto.response.StoreCollection;
import app.domain.forecast.repository.ForecastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final FastApiClient fastApiClient;
    private final ForecastRepository forecastRepository;
    private final StoreInternalApiClient storeInternalApiClient;

    public GetForecastResponse getForecast(GetForecastRequest request) {
        // 1. mongoDB에서 직전 InputLength(시간)만큼의 데이터를 가져옴
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusHours(request.getInputHours());
        List<ForecastDocument> forecastDocumentList = forecastRepository.findByStoreIdAndTimestampBetween(request.getStoreId(), startDate, endDate);

        // 2. Store 서비스에서 가게 정보(가게 이름, 카테고리, 지역, 최소주문금액, 평점) 가져옴
        ApiResponse<StoreCollection> storeResponse = storeInternalApiClient.getStoreByKey(request.getStoreId());
        StoreCollection storeCollection = storeResponse.result();

        // 3. FastAPI 호출하여 예측 결과 받음
        List<RealDataItem> realDataItems = forecastDocumentList.stream()
            .filter(doc -> doc.getRealOrderQuantity() != null && doc.getRealSalesRevenue() != null)
            .map(doc -> RealDataItem.builder()
                .timestamp(doc.getTimestamp())
                .storeId(doc.getStoreId())
                .categoryMain(storeCollection.getCategoryKeys().get(0))
                .categorySub(storeCollection.getCategoryKeys().get(1))
                .categoryItem(storeCollection.getCategoryKeys().get(2))
                .region(storeCollection.getRegionName())
                .realOrderQuantity(doc.getRealOrderQuantity())
                .realSalesRevenue(doc.getRealSalesRevenue())
                .dayOfWeek(doc.getTimestamp().getDayOfWeek().getValue())
                .hour(doc.getTimestamp().getHour())
                .minOrderAmount(storeCollection.getMinOrderAmount().intValue())
                .avgRating(storeCollection.getAvgRating().intValue())
                .build())
            .collect(Collectors.toList());

        FastApiRequest fastApiRequest = FastApiRequest.builder()
                .storeId(request.getStoreId())
                .inputLength(request.getInputHours())
                .predictionLength(request.getPredictionHours())
                .realDataItemList(realDataItems)
                .build();
        FastApiResponse fastApiResponse = fastApiClient.predict(fastApiRequest);

        // 4. 예측 결과를 MongoDB에 저장
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        fastApiResponse.getPredictions().forEach(prediction -> {
            LocalDateTime timestamp = LocalDateTime.parse(prediction.getTimestamp(), formatter);
            String storeId = fastApiResponse.getStoreId();

            Optional<ForecastDocument> existingDocumentOpt = forecastRepository.findByStoreIdAndTimestamp(storeId, timestamp);

            if (existingDocumentOpt.isPresent()) {
                ForecastDocument existingDocument = existingDocumentOpt.get();
                existingDocument.setPredOrderQuantity(prediction.getPredOrderQuantity());
                existingDocument.setPredSalesRevenue(prediction.getPredSalesRevenue());
                forecastRepository.save(existingDocument);
            } else {
                ForecastDocument forecastDocument = ForecastDocument.builder()
                        .storeId(storeId)
                        .timestamp(timestamp)
                        .predOrderQuantity(prediction.getPredOrderQuantity())
                        .predSalesRevenue(prediction.getPredSalesRevenue())
                        .build();
                forecastRepository.save(forecastDocument);
            }
        });

        // 5. 최종 응답 객체 생성 및 반환
        List<Map<String, Object>> hourlySalesRevenue = new java.util.ArrayList<>();
        List<Map<String, Object>> hourlyOrderQuantity = new java.util.ArrayList<>();

        for (FastApiResponse.Prediction prediction : fastApiResponse.getPredictions()) {
            Map<String, Object> salesMap = new java.util.HashMap<>();
            salesMap.put("timestamp", prediction.getTimestamp());
            salesMap.put("pred_sales_revenue", prediction.getPredSalesRevenue());
            hourlySalesRevenue.add(salesMap);

            Map<String, Object> quantityMap = new java.util.HashMap<>();
            quantityMap.put("timestamp", prediction.getTimestamp());
            quantityMap.put("pred_order_quantity", prediction.getPredOrderQuantity());
            hourlyOrderQuantity.add(quantityMap);
        }

        return GetForecastResponse.builder()
                .storeName(storeCollection.getStoreName())
                .hourlySalesRevenue(hourlySalesRevenue)
                .hourlyOrderQuantity(hourlyOrderQuantity)
                .build();
    }
}

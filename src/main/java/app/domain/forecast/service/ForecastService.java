package app.domain.forecast.service;

import app.commonUtil.apiPayload.ApiResponse;
import app.domain.forecast.client.FastApiClient;
import app.domain.forecast.client.StoreInternalApiClient;
import app.domain.forecast.document.ForecastDocument;
import app.domain.forecast.model.dto.request.FastApiRequest;
import app.domain.forecast.model.dto.request.GetForecastRequest;
import app.domain.forecast.model.dto.response.FastApiResponse;
import app.domain.forecast.model.dto.response.GetForecastResponse;
import app.domain.forecast.model.dto.response.StoreServiceStoreInfo;
import app.domain.forecast.model.dto.request.RealDataItem;
import app.domain.forecast.repository.ForecastRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final FastApiClient fastApiClient;
    private final ForecastRepository forecastRepository;
    private final StoreInternalApiClient storeInternalApiClient;

    /**
     * 전체 예측 프로세스를 조율하는 메인 메서드
     */
    public GetForecastResponse getForecast(GetForecastRequest request) {
        // 1. mongoDB에서 직전 일주일 데이터를 가져옴
        List<ForecastDocument> historicalData = fetchRealData(request.getStoreId(), request.getPredictionLength());

        // 2. Store 서비스에서 가게 정보(가게 이름, 카테고리, 지역, 최소주문금액, 평점) 조회
        StoreServiceStoreInfo storeServiceStoreInfo = fetchStoreInfo(request.getStoreId());

        // 3. FastAPI 호출하여 예측 결과 받기
        FastApiResponse fastApiResponse = callPredictingApi(request, historicalData, storeServiceStoreInfo);

        // 4. 예측 결과를 DB에 저장
        savePredictions(fastApiResponse);

        // 5. 최종 응답 객체 생성 및 반환
        return buildForecastResponse(storeServiceStoreInfo.getStoreName(), fastApiResponse);
    }

    private List<ForecastDocument> fetchRealData(String storeId, int inputLength) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(inputLength);

        return forecastRepository.findByStoreIdAndTimestampBetween(storeId, startDate, endDate);
    }

    private StoreServiceStoreInfo fetchStoreInfo(String storeId) {
        ApiResponse<StoreServiceStoreInfo> response = storeInternalApiClient.getStoreByKey(storeId);

        return response.result();
    }

    private FastApiResponse callPredictingApi(GetForecastRequest request, List<ForecastDocument> historicalData, StoreServiceStoreInfo storeInfo) {
        List<RealDataItem> realDataItems = historicalData.stream()
            .map(doc -> RealDataItem.builder()
                .timestamp(doc.getTimestamp().atStartOfDay())
                .storeId(doc.getStoreId())
                .categoryMain(storeInfo.getCategoryMain())
                .categorySub(storeInfo.getCategorySub())
                .categoryItem(storeInfo.getCategoryItem())
                .region(storeInfo.getRegion())
                .realOrderQuantity(doc.getRealOrderQuantity().intValue())
                .realSalesRevenue(doc.getRealSalesRevenue().intValue())
                .dayOfWeek(doc.getTimestamp().getDayOfWeek().getValue())
                .hour(doc.getTimestamp().atStartOfDay().getHour())
                .minOrderAmount(storeInfo.getMinOrderAmount())
                .avgRating(storeInfo.getAvgRating())
                .build())
            .collect(Collectors.toList());


        FastApiRequest fastApiRequest = FastApiRequest.builder()
                .storeId(request.getStoreId())
                .inputLength(request.getInputLength())
                .predictionLength(request.getPredictionLength())
                .realDataItemList(realDataItems)
                .build();

        return fastApiClient.predict(fastApiRequest);
    }

    private void savePredictions(FastApiResponse response) {

    }

    private GetForecastResponse buildForecastResponse(String storeName, FastApiResponse response) {
        return GetForecastResponse.builder()
                .storeName(storeName)
                .forecast(response)
                .build();
    }

}

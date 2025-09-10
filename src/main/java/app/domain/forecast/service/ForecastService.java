package app.domain.forecast.service;

import app.commonUtil.apiPayload.ApiResponse;
import app.domain.forecast.client.FastApiClient;
import app.domain.forecast.client.OrderInternalApiClient;

import app.domain.forecast.model.dto.request.FastApiRequest;
import app.domain.forecast.model.dto.response.FastApiResponse;
import app.domain.forecast.model.dto.response.ForecastAnalyticsResponse;
import app.domain.forecast.model.dto.response.OrderServiceStoreOrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final FastApiClient fastApiClient;
    
    private final OrderInternalApiClient orderInternalApiClient;

    public ForecastAnalyticsResponse getForecastAndAnalytics(String storeId, int predictionLength, boolean fineTune) {
        // 1. Get forecast from FastAPI
        FastApiRequest request = FastApiRequest.builder()
                .storeId(storeId)
                .predictionLength(predictionLength)
                .fineTune(fineTune)
                .build();
        FastApiResponse forecastResponse = fastApiClient.predict(request);

        // 3. Get order data from order-service
        ApiResponse<List<OrderServiceStoreOrderInfo>> orderInfoResponse = orderInternalApiClient.getOrdersByStoreId(UUID.fromString(storeId));
        List<OrderServiceStoreOrderInfo> orderInfos = orderInfoResponse.result();

        // 4. Process order data for analytics
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twelveHoursAgo = now.minusHours(12);

        Map<Integer, Long> hourlyRevenue = orderInfos.stream()
                .filter(order -> order.getOrderedAt() != null && order.getOrderedAt().isAfter(twelveHoursAgo))
                .collect(Collectors.groupingBy(
                        order -> order.getOrderedAt().getHour(),
                        Collectors.summingLong(OrderServiceStoreOrderInfo::getTotalPrice)
                ));

        Map<Integer, Long> hourlyOrderVolume = orderInfos.stream()
                .filter(order -> order.getOrderedAt() != null && order.getOrderedAt().isAfter(twelveHoursAgo))
                .collect(Collectors.groupingBy(
                        order -> order.getOrderedAt().getHour(),
                        Collectors.counting()
                ));
        
        List<Map<String, Object>> hourlyRevenueList = hourlyRevenue.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hour", entry.getKey());
                    map.put("revenue", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> hourlyOrderVolumeList = hourlyOrderVolume.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hour", entry.getKey());
                    map.put("volume", entry.getValue());
                    return map;
                })
                .collect(Collectors.toList());


        // 5. Build and return combined response
        return ForecastAnalyticsResponse.builder()
                .forecast(forecastResponse)
                .hourlyRevenue(hourlyRevenueList)
                .hourlyOrderVolume(hourlyOrderVolumeList)
                .build();
    }
}
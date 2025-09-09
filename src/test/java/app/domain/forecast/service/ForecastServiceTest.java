package app.domain.forecast.service;

import app.commonUtil.apiPayload.ApiResponse;
import app.commonUtil.apiPayload.code.status.SuccessStatus;
import app.domain.forecast.client.FastApiClient;
import app.domain.forecast.client.OrderInternalApiClient;
import app.domain.forecast.document.ForecastPrediction;
import app.domain.forecast.model.dto.response.FastApiResponse;
import app.domain.forecast.model.dto.response.ForecastAnalyticsResponse;
import app.domain.forecast.model.dto.response.OrderServiceStoreOrderInfo;
import app.domain.forecast.repository.ForecastPredictionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {

    @InjectMocks
    private ForecastService forecastService;

    @Mock
    private ForecastPredictionRepository forecastPredictionRepository;

    @Mock
    private FastApiClient fastApiClient;

    @Mock
    private OrderInternalApiClient orderInternalApiClient;

    @Test
    @DisplayName("수요 예측 및 분석 서비스 성공 테스트")
    void getForecastAndAnalytics_Success() {
        // given
        String storeIdStr = UUID.randomUUID().toString();
        int predictionLength = 24;
        boolean fineTune = false;

        // Mock data for FastApiResponse
        FastApiResponse.Prediction prediction = new FastApiResponse.Prediction("2025-09-09T12:00:00", 150.5);
        FastApiResponse mockFastApiResponse = new FastApiResponse(storeIdStr, Collections.singletonList(prediction), predictionLength, "2025-09-08T23:00:00");

        // Mock data for OrderInternalApiClient
        OrderServiceStoreOrderInfo orderInfo = OrderServiceStoreOrderInfo.builder()
                .orderId(UUID.randomUUID())
                .storeId(UUID.fromString(storeIdStr))
                .totalPrice(50000L)
                .orderedAt(LocalDateTime.now().minusHours(1))
                .build();
        ApiResponse<List<OrderServiceStoreOrderInfo>> mockOrderApiResponse = ApiResponse.onSuccess(SuccessStatus._OK, Collections.singletonList(orderInfo));

        // Mocking client and repository calls
        when(fastApiClient.predict(any())).thenReturn(mockFastApiResponse);
        when(orderInternalApiClient.getOrdersByStoreId(any(UUID.class))).thenReturn(mockOrderApiResponse);
        when(forecastPredictionRepository.saveAll(any(List.class))).thenReturn(Collections.emptyList()); // or mock the saved list

        // when
        ForecastAnalyticsResponse response = forecastService.getForecastAndAnalytics(storeIdStr, predictionLength, fineTune);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getForecast()).isEqualTo(mockFastApiResponse);
        assertThat(response.getHourlyRevenue()).isNotNull();
        assertThat(response.getHourlyOrderVolume()).isNotNull();

        // verify that the methods were called
        verify(fastApiClient, times(1)).predict(any());
        verify(orderInternalApiClient, times(1)).getOrdersByStoreId(UUID.fromString(storeIdStr));
        verify(forecastPredictionRepository, times(1)).saveAll(any(List.class));
    }
}
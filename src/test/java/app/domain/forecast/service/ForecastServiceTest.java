package app.domain.forecast.service;

import app.commonUtil.apiPayload.ApiResponse;
import app.domain.forecast.client.FastApiClient;
import app.domain.forecast.client.StoreInternalApiClient;
import app.domain.forecast.document.ForecastDocument;
import app.domain.forecast.model.dto.request.GetForecastRequest;
import app.domain.forecast.model.dto.response.FastApiResponse;
import app.domain.forecast.model.dto.response.GetForecastResponse;
import app.domain.forecast.model.dto.response.StoreCollection;
import app.domain.forecast.repository.ForecastRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {

    @InjectMocks
    private ForecastService forecastService;

    @Mock
    private ForecastRepository forecastRepository;

    @Mock
    private FastApiClient fastApiClient;

    @Mock
    private StoreInternalApiClient storeInternalApiClient;

    @Test
    @DisplayName("수요 예측 서비스 성공 테스트")
    void getForecast_Success() {
        // given
        GetForecastRequest request = GetForecastRequest.builder()
                .storeId("store_1")
                .inputHours(168)
                .predictionHours(24)
                .fineTune(false)
                .build();

        // Mock for forecastRepository
        ForecastDocument mockDoc = ForecastDocument.builder()
                .timestamp(LocalDateTime.now())
                .storeId(request.getStoreId())
                .realOrderQuantity(10)
                .realSalesRevenue(150000)
                .build();
        when(forecastRepository.findByStoreIdAndTimestampBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(mockDoc));

        // Mock for storeInternalApiClient
        StoreCollection mockStoreCollection = new StoreCollection("테스트 가게", List.of("치킨", "한식", "양식"), "서울", 10000, 4.5);
        ApiResponse<StoreCollection> mockStoreApiResponse = ApiResponse.onSuccess(mockStoreCollection);
        when(storeInternalApiClient.getStoreByKey(anyString())).thenReturn(mockStoreApiResponse);

        // Mock for fastApiClient
        FastApiResponse.Prediction prediction = new FastApiResponse.Prediction("2025-09-10T12:00:00", 15, 225000);
        FastApiResponse mockFastApiResponse = new FastApiResponse(request.getStoreId(), request.getPredictionHours(), Collections.singletonList(prediction));
        when(fastApiClient.predict(any())).thenReturn(mockFastApiResponse);

        // when
        GetForecastResponse response = forecastService.getForecast(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStoreName()).isEqualTo("테스트 가게");
        assertThat(response.getHourlySalesRevenue()).hasSize(1);
        assertThat(response.getHourlyOrderQuantity()).hasSize(1);
        assertThat(response.getHourlySalesRevenue().get(0).get("pred_sales_revenue")).isEqualTo(225000);

        // verify that the methods were called
        verify(forecastRepository, times(1)).findByStoreIdAndTimestampBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(storeInternalApiClient, times(1)).getStoreByKey(request.getStoreId());
        verify(fastApiClient, times(1)).predict(any());
        verify(forecastRepository, times(1)).save(any(ForecastDocument.class)); // verify that save is called for the prediction
    }
}

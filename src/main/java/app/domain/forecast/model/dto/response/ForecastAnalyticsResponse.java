package app.domain.forecast.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ForecastAnalyticsResponse {

    private FastApiResponse forecast;
    private List<Map<String, Object>> hourlyRevenue;
    private List<Map<String, Object>> hourlyOrderVolume;
}

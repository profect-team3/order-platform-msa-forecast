package app.domain.forecast.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class GetForecastResponse {
    private String storeName;
    private List<Map<String, Object>> hourlySalesRevenue;
    private List<Map<String, Object>> hourlyOrderQuantity;
}

package app.domain.forecast.model.stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreHourlyAggregation {
    private Long orderCount;
    private Double totalRevenue;
}

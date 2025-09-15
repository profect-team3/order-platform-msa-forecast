package app.domain.forecast.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "store_hourly_statistics")
public class StoreHourlyStatistic {

    @Id
    private String id;

    private String storeId;
    private LocalDateTime timestamp;

    // Predicted values (can be populated by another process)
    private Long predOrderQuantity;
    private Double predSalesRevenue;

    // Real-time aggregated values
    private Long realOrderQuantity;
    private Double realSalesRevenue;
}

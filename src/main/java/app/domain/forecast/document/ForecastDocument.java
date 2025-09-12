package app.domain.forecast.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.TimeSeries;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@TimeSeries(collection = "forecasts", timeField = "timestamp", metaField = "store_id")
public class ForecastDocument {

    @Id
    private String id;

    @Field("store_id")
    private String storeId;

    @Field("timestamp")
    private LocalDateTime timestamp;

    // FastAPI 예측 값
    @Field("pred_order_quantity")
    private Integer predOrderQuantity;

    @Field("pred_sales_revenue")
    private Integer predSalesRevenue;

    // Kafka 실제 값
    @Field("real_order_quantity")
    private Integer realOrderQuantity;

    @Field("real_sales_revenue")
    private Integer realSalesRevenue;
}

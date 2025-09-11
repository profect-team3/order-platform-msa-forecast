package app.domain.forecast.document;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Document(collection = "forecasts")
public class ForecastDocument {

    @Id
    private String id;

    @Field("store_id")
    private String storeId;

    @Field("timestamp")
    private LocalDate Timestamp;

    // FastAPI 예측 값
    @Field("pred_order_quantity")
    private Double predOrderQuantity;

    @Field("pred_sales_revenue")
    private Double predSalesRevenue;

    // Kafka 실제 값
    @Field("real_order_quantity")
    private Double realOrderQuantity;

    @Field("real_sales_revenue")
    private Double realSalesRevenue;
}

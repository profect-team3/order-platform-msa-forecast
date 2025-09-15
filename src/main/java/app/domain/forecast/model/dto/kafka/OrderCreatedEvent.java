package app.domain.forecast.model.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {
    private UUID storeId;
    private Double totalPrice;
    private String region;
    private Double avg_rating;
    private Integer min_order_amount;
    private String category_main;
    private String category_sub;
    private String category_item;
}

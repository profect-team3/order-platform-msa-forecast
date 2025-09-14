package app.domain.forecast.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FastApiResponse {

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("prediction_length")
    private int predictionLength;

    @JsonProperty("predictions")
    private List<Prediction> predictions;


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Prediction {
        @JsonProperty("timestamp")
        private String timestamp;

        @JsonProperty("pred_order_quantity")
        private int predOrderQuantity;

        @JsonProperty("pred_sales_revenue")
        private int predSalesRevenue;
    }
}
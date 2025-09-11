package app.domain.forecast.model.dto.request;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FastApiRequest {

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("prediction_length")
    private int predictionLength;

    private List<PredictionDataItem> predictionDataItemList;
}
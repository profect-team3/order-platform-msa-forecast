package app.domain.forecast.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetForecastRequest {
    private String storeId;
    private int inputLength;
    private int predictionLength;
    private boolean fineTune;
}

package app.domain.forecast.model.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetForecastRequest {
    private String storeId;
    private int predictionLength;
    private boolean fineTune;
}

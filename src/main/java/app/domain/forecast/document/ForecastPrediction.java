package app.domain.forecast.document;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "forecast_predictions")
@Getter
@Builder
public class ForecastPrediction {

    @Id
    private String id;

    @Field("store_id")
    private String storeId;

    @Field("prediction_timestamp")
    private String predictionTimestamp; // from fastapi response

    @Field("value")
    private double value; // 'mean' value from fastapi response

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
}

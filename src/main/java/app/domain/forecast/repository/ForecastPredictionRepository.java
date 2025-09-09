package app.domain.forecast.repository;

import app.domain.forecast.document.ForecastPrediction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForecastPredictionRepository extends MongoRepository<ForecastPrediction, String> {
}

package app.domain.forecast.repository;

import app.domain.forecast.document.ForecastDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ForecastRepository extends MongoRepository<ForecastDocument, String> {

    List<ForecastDocument> findByStoreIdAndTimestampBetween(String storeId, LocalDateTime startDate, LocalDateTime endDate);
}

package app.domain.forecast.repository;

import app.domain.forecast.document.ForecastDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ForecastRepository extends MongoRepository<ForecastDocument, String> {
    Optional<ForecastDocument> findByStoreIdAndTimestamp(String storeId, LocalDateTime timestamp);
    List<ForecastDocument> findByStoreIdAndTimestampBetween(String storeId, LocalDateTime startDate, LocalDateTime endDate);
}

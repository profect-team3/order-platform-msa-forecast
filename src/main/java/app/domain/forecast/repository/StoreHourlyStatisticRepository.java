package app.domain.forecast.repository;

import app.domain.forecast.document.StoreHourlyStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StoreHourlyStatisticRepository extends MongoRepository<StoreHourlyStatistic, String> {
    Optional<StoreHourlyStatistic> findByStoreIdAndTimestamp(Long storeId, LocalDateTime timestamp);
}

package app;

import app.domain.forecast.document.ForecastDocument;
import app.domain.forecast.repository.ForecastRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@EnableFeignClients
@SpringBootApplication
public class ForecastApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForecastApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(ForecastRepository forecastRepository) {
		return args -> {
			forecastRepository.deleteAll();

			List<ForecastDocument> documents = new ArrayList<>();
			final String storeId = "87654321-fedc-ba98-7654-3210fedcba08";
			final int totalHours = 24 * 7; // 7 days of historical data
			final int AVG_PRICE_PER_ORDER = 15000;
			LocalDateTime now = LocalDateTime.now();
			Random random = new Random();

			for (int i = 0; i < totalHours; i++) {
				LocalDateTime timestamp = now.minusHours(i + 24).truncatedTo(ChronoUnit.HOURS);
				int hour = timestamp.getHour();

				// Determine base order quantity based on time of day
				int baseQuantity;
				if (hour >= 11 && hour <= 13) { // Lunch peak
					baseQuantity = 20;
				} else if (hour >= 17 && hour <= 20) { // Dinner peak
					baseQuantity = 30;
				} else { // Off-peak
					baseQuantity = 5;
				}

				// Generate real values
				int realQuantity = baseQuantity + random.nextInt(5) - 2; // Fluctuation of -2 to +2
				realQuantity = Math.max(0, realQuantity); // Ensure non-negative

				int realRevenue = realQuantity * (AVG_PRICE_PER_ORDER + random.nextInt(2001) - 1000); // Price fluctuation

				ForecastDocument doc = ForecastDocument.builder()
						.storeId(storeId)
						.timestamp(timestamp)
						.realOrderQuantity(realQuantity)
						.realSalesRevenue(realRevenue)
						.build();

				documents.add(doc);
			}

			forecastRepository.saveAll(documents);
			System.out.println(documents.size() + " sample forecast data points inserted for store '" + storeId + "'.");
		};
	}
}

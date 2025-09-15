package app.domain.forecast.model.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RealDataItem {

	private LocalDateTime timestamp;

	private String storeId;

	private String categoryMain;

	private String categorySub;

	private String categoryItem;

	private String region;

	private int realOrderQuantity;

	private Long realSalesRevenue;

	private int dayOfWeek;

	private int hour;

	private int minOrderAmount;

	private double avgRating;
}

package app.domain.forecast.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastRequest {
    private Long userId;
    private LocalDateTime requestTime;
    private List<OrderRecord> orderRecords; // 주문 기록 리스트

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderRecord {
        private String orderId;
        private LocalDateTime orderTime;
        private Double amount;
        // 필요한 다른 필드들
    }
}

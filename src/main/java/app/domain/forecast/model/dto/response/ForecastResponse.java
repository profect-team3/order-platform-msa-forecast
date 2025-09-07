package app.domain.forecast.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ForecastResponse {
    private String prediction; // 예측값
    private Map<String, Object> statistics; // 통계 정보
}

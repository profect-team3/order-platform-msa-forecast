package app.domain.forecast.controller;

import app.domain.forecast.model.dto.request.GetForecastRequest;
import app.domain.forecast.model.dto.response.ForecastAnalyticsResponse;
import app.domain.forecast.service.ForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping
    public ResponseEntity<ForecastAnalyticsResponse> getForecast(@RequestBody GetForecastRequest request) {
        ForecastAnalyticsResponse response = forecastService.getForecastAndAnalytics(
                request.getStoreId(),
                request.getPredictionLength(),
                request.isFineTune()
        );
        return ResponseEntity.ok(response);
    }
}

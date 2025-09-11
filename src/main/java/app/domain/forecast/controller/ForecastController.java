package app.domain.forecast.controller;

import app.domain.forecast.model.dto.request.GetForecastRequest;
import app.domain.forecast.model.dto.response.GetForecastResponse;
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
    public ResponseEntity<GetForecastResponse> getForecast(@RequestBody GetForecastRequest request) {
        GetForecastResponse response = forecastService.getForecast(GetForecastRequest request);
        return ResponseEntity.ok(response);
    }
}

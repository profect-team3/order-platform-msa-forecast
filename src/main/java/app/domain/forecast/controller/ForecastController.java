package app.domain.forecast.controller;

import app.domain.forecast.model.dto.response.ForecastResponse;
import app.domain.forecast.service.ForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.commonUtil.apiPayload.ApiResponse;
import app.commonUtil.apiPayload.exception.GeneralException;
import app.commonUtil.security.TokenPrincipalParser;

@RestController
@RequestMapping("/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping()
    public ResponseEntity<ForecastResponse> generateDescription(Authentication authentication) {
        ForecastResponse response = forecastService.generateDescription(authentication);
        return ResponseEntity.ok(response);
    }
}

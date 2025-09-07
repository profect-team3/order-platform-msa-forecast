package app.domain.forecast.client;

import app.domain.forecast.model.dto.request.ForecastRequest;
import app.domain.forecast.model.dto.response.ForecastResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "fastApiClient", url = "http://localhost:8000")
public interface FastApiClient {

    @PostMapping("/predict")
    ForecastResponse predict(@RequestBody ForecastRequest request);
}

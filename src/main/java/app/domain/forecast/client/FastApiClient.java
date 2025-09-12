package app.domain.forecast.client;

import app.domain.forecast.model.dto.request.FastApiRequest;
import app.domain.forecast.model.dto.response.FastApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "fastApiClient", url = "http://localhost:8099")
public interface FastApiClient {

    @PostMapping("/predict")
    FastApiResponse predict(@RequestBody FastApiRequest request);
}

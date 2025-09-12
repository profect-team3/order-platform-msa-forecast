package app.domain.forecast.client;

import app.commonUtil.apiPayload.ApiResponse;
import app.domain.forecast.model.dto.response.StoreCollection;

import app.config.FeignClientAuthConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "store-internal-api", url = "http://localhost:8082", configuration = FeignClientAuthConfig.class)
public interface StoreInternalApiClient {

    @GetMapping("/store/mongo/{storeId}")
    ApiResponse<StoreCollection> getStoreByKey(@PathVariable("storeId") String storeKey);
}

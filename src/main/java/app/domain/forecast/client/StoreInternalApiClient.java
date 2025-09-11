package app.domain.forecast.client;

import app.commonUtil.apiPayload.ApiResponse;
import app.domain.forecast.model.dto.response.StoreServiceStoreInfo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "store")
public interface StoreInternalApiClient {

    @GetMapping("/internal/store/mongo/{storeId}")
    ApiResponse<StoreServiceStoreInfo> getStoreByKey(@PathVariable("storeId") String storeKey);
}

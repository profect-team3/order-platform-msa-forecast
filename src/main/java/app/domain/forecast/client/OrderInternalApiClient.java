package app.domain.forecast.client;

import app.commonUtil.apiPayload.ApiResponse;
import app.domain.forecast.model.dto.response.OrderServiceStoreOrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order")
public interface OrderInternalApiClient {

    @GetMapping("/internal/order/store/{storeId}")
    ApiResponse<List<OrderServiceStoreOrderInfo>> getOrdersByStoreId(@PathVariable("storeId") UUID storeId);
}

package app.domain.forecast.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreServiceStoreInfo {
    private String storeName;
    private String categoryMain;
    private String categorySub;
    private String categoryItem;
    private String region;
    private int minOrderAmount;
    private int avgRating;
}

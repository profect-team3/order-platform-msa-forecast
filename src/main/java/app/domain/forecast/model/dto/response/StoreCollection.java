package app.domain.forecast.model.dto.response;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
public class StoreCollection {
	@Id
	private String id;
	private Long userId;
	@Indexed(name = "store_key_index", unique = true)
	private String storeKey;
	@TextIndexed(weight = 3)
	private String storeName;
	private String description;
	@TextIndexed(weight = 2)
	private List<String> categoryKeys;
	private Double avgRating;
	private Long reviewCount;
	private String phoneNumber;
	private Long minOrderAmount;
	private String address;
	private String regionName;
	private String regionFullName;
	private String storeAcceptStatus;
	private Boolean isActive;
	private Date createdAt;
	@Indexed
	private Date updatedAt;
	private Date deletedAt;

	@Field("version")
	@org.springframework.data.annotation.Version
	private Long version = 0L;
	@TextIndexed
	private List<MenuCollection> menus;
}
package app.domain.forecast.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuCollection {
	private String menuId;
	private String name;
	private Integer price;
	private String description;
	private String category;
	private boolean isHidden = false;
}
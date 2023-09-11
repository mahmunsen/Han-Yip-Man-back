package com.supercoding.hanyipman.dto.Shop.buyer.response;

import com.supercoding.hanyipman.entity.Menu;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.supercoding.hanyipman.entity.Menu}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuByMenuGroupResponse implements Serializable {
    private Long menuId;
    private String menuName;
    private Integer menuPrice;
    private Integer menuDiscountPrice;
    private String menuDescription;
    private String menuThumbnailUrl;

    public static MenuByMenuGroupResponse from(Menu menu) {
        return MenuByMenuGroupResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getName())
                .menuPrice(menu.getPrice())
                .menuDiscountPrice(menu.getDiscountPrice())
                .menuDescription(menu.getDescription())
                .menuThumbnailUrl(menu.getImageUrl())
                .build();
    }

}
package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.MenuGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuGroupResponse {

    private Long menuGroupId;
    private String menuGroupName;

    public static MenuGroupResponse from(MenuGroup menuGroup) {
        return MenuGroupResponse.builder()
                .menuGroupId(menuGroup.getId())
                .menuGroupName(menuGroup.getName())
                .build();
    }

}

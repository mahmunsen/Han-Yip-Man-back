package com.supercoding.hanyipman.dto.Shop.buyer.response;

import com.supercoding.hanyipman.entity.Menu;
import com.supercoding.hanyipman.entity.MenuGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuGroupListResponse {

    private Long menuGroupId;
    private String menuGroupName;

    private List<MenuByMenuGroupResponse> menuByMenuGroupList;

    public static MenuGroupListResponse from(MenuGroup menuGroup) {
        return MenuGroupListResponse.builder()
                .menuGroupId(menuGroup.getId())
                .menuGroupName(menuGroup.getName())
                .menuByMenuGroupList(menuGroup.getMenus().stream()
                        .filter(menu -> !menu.getIsDeleted())
                        .sorted(Comparator.comparing(Menu::getSequence))
                        .map(MenuByMenuGroupResponse::from).collect(Collectors.toList()))
                .build();
    }

}

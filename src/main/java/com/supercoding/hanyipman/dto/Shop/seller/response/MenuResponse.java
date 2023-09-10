package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.Menu;
import com.supercoding.hanyipman.entity.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuResponse {

    private Long menuId;
    private String menuName;
    private Integer menuPrice;
    private String menuDescription;
    private String menuThumbnailUrl;
    private List<OptionResponse> optionResponses;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getName())
                .menuPrice(menu.getPrice())
                .menuDescription(menu.getDescription())
                .menuThumbnailUrl(menu.getImageUrl())
                .optionResponses(menu.getOptions().stream().map(OptionResponse::from).collect(Collectors.toList()))
                .build();
    }

}

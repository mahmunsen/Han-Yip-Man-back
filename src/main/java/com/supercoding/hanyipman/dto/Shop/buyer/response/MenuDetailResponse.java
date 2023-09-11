package com.supercoding.hanyipman.dto.Shop.buyer.response;


import com.supercoding.hanyipman.entity.Menu;
import com.supercoding.hanyipman.entity.Option;
import com.supercoding.hanyipman.entity.OptionItem;
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
public class MenuDetailResponse {

    private Long menuId;
    private String menuName;
    private String menuDescription;
    private String thumbnailUrl;
    private Integer menuPrice;
    private Integer discountPrice;
    private List<DetailOptionResponse> options;

    public static MenuDetailResponse from(Menu menu) {
        return MenuDetailResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getName())
                .menuDescription(menu.getDescription())
                .thumbnailUrl(menu.getImageUrl())
                .menuPrice(menu.getPrice())
                .discountPrice(menu.getDiscountPrice())
                .options(menu.getOptions()
                        .stream()
                        .map(DetailOptionResponse::from)
                        .collect(Collectors.toList())
                )
                .build();
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DetailOptionResponse{
        private Long optionId;
        private String optionName;
        private Boolean isMultiple;
        private Integer maxSelected;

        private List<DetailOptionItemResponse> optionItems;

        private static DetailOptionResponse from(Option option) {
            return DetailOptionResponse.builder()
                    .optionId(option.getId())
                    .optionName(option.getName())
                    .isMultiple(option.getIsMultiple())
                    .maxSelected(option.getMaxSelected())
                    .optionItems(option.getOptionItems()
                            .stream()
                            .map(DetailOptionItemResponse::from)
                            .collect(Collectors.toList()))
                    .build();
        }

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        private static class DetailOptionItemResponse{
            private Long optionItemId;
            private String optionItemName;
            private Integer optionItemPrice;

            private static DetailOptionItemResponse from(OptionItem optionItem) {
                return DetailOptionItemResponse.builder()
                        .optionItemId(optionItem.getId())
                        .optionItemName(optionItem.getName())
                        .optionItemPrice(optionItem.getPrice())
                        .build();
            }
        }

    }

}

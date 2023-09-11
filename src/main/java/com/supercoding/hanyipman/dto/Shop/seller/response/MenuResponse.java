package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.Menu;
import com.supercoding.hanyipman.entity.Option;
import com.supercoding.hanyipman.entity.OptionItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
    private List<OptionResponse> options;

    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .menuName(menu.getName())
                .menuPrice(menu.getPrice())
                .menuDescription(menu.getDescription())
                .menuThumbnailUrl(menu.getImageUrl())
                .options(menu.getOptions().stream().map(OptionResponse::from).collect(Collectors.toList()))
                .build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionResponse {
        private Long optionId;
        private String optionName;
        private Integer maxSelected;
        private Boolean isMultiple;
        private List<OptionItemDto> optionItems;

        public static OptionResponse from(Option option) {
            return OptionResponse.builder()
                    .optionId(option.getId())
                    .optionName(option.getName())
                    .maxSelected(option.getMaxSelected())
                    .isMultiple(option.getIsMultiple())
                    .optionItems(option.getOptionItems().stream().map(OptionItemDto::from).collect(Collectors.toList()))
                    .build();
        }

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OptionItemDto implements Serializable {
            private Long optionItemId;
            private String optionItemName;
            private Integer optionItemPrice;

            public static OptionItemDto from(OptionItem optionItem) {
                return OptionItemDto.builder()
                        .optionItemId(optionItem.getId())
                        .optionItemName(optionItem.getName())
                        .optionItemPrice(optionItem.getPrice())
                        .build();
            }
        }
    }
}

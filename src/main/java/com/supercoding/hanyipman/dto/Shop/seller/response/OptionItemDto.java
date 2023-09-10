package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.OptionItem;
import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionItemDto implements Serializable {
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
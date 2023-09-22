package com.supercoding.hanyipman.dto.cart.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.supercoding.hanyipman.entity.CartOptionItem;
import com.supercoding.hanyipman.entity.OptionItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class OptionItemResponse {
    @JsonIgnore
    private final Long cartId;
    private final Long optionItemId;
    private final String optionItemName;
    private final Integer optionItemPrice;

    public static OptionItemResponse from(CartOptionItem optionItem) {
        return OptionItemResponse.builder()
                .cartId(optionItem.getCart().getId())
                .optionItemId(optionItem.getId())
                .optionItemName(optionItem.getOptionItem().getName())
                .optionItemPrice(optionItem.getOptionItem().getPrice())
                .build();
    }
}

package com.supercoding.hanyipman.dto.cart.response;

import com.supercoding.hanyipman.entity.Cart;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ViewCartResponse {
    private final Long cartId;
    private final String menuName;
    private final Integer menuPrice;
    private final Integer amount;
    private List<OptionItemResponse> optionItems;
    private Long totalPrice;

    public static ViewCartResponse from(Cart cart) {
        return ViewCartResponse.builder()
                .cartId(cart.getId())
                .menuName(cart.getMenu().getName())
                .menuPrice(cart.getMenu().getPrice())
                .amount(cart.getAmount().intValue())
                .optionItems(new ArrayList<>())
                .totalPrice(0L)
                .build();
    }

    public void calTotalPrice(){
        Integer price = menuPrice;
        if(optionItems == null) {
            this.optionItems = new ArrayList<>();
        }
        price += optionItems.stream()
                .mapToInt(OptionItemResponse::getOptionPrice)
                .sum();
        this.totalPrice =  price * amount.longValue();
    }
}

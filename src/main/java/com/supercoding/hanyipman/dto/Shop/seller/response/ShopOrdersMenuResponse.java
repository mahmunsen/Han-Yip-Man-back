package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.Cart;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "주문 메뉴 메뉴 응답 DTO")
public class ShopOrdersMenuResponse {
    @ApiModelProperty(value="주문 메뉴 이름 필드", dataType = "String")
    public String menuName;
    @ApiModelProperty(value="주문 메뉴 옵션 리스트 필드", dataType = "List<String>")
    public List<String> optionNames;

    public static ShopOrdersMenuResponse from(Cart cart) {
        return ShopOrdersMenuResponse.builder()
                .menuName(cart.getMenu().getName())
                .optionNames(cart.getCartOptionItems().stream().map(cartOptionItem -> cartOptionItem.getOptionItem().getName()).collect(Collectors.toList()))
                .build();
    }

}

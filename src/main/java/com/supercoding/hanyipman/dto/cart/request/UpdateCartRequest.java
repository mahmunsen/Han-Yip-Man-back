package com.supercoding.hanyipman.dto.cart.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateCartRequest {
    @ApiModelProperty(value = "장바구니 ID", example = "5")
    private final Long cartId;
    @ApiModelProperty(value = "특정 장바구니 메뉴 수량", example = "3")
    private final Long amount;
}

package com.supercoding.hanyipman.dto.order.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOrderRequest {
    @ApiModelProperty(value = "구매자 쿠폰 ID", example = "1")
    private Long buyerCouponId;
}

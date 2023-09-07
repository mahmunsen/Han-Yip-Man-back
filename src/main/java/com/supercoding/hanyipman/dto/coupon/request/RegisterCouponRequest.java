package com.supercoding.hanyipman.dto.coupon.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCouponRequest {

    @ApiModelProperty(value = "쿠폰 코드", example = "사장님이 미쳤어요")
    private String couponCode;

}

package com.supercoding.hanyipman.dto.coupon.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "쿠폰 등록 요청 DTO")
public class RegisterCouponRequest {

    @ApiModelProperty(value = "쿠폰 코드", example = "사장님이 미쳤어요")
    private String couponCode;

}

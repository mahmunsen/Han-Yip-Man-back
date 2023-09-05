package com.supercoding.hanyipman.dto.coupon.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterCouponRequest {

    @ApiModelProperty(value = "쿠폰 코드", example = "사장님이 미쳤어요")
    private String couponCode;

}

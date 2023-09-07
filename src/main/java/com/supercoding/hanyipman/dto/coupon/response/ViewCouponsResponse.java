package com.supercoding.hanyipman.dto.coupon.response;

import com.supercoding.hanyipman.entity.BuyerCoupon;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "쿠폰 조회 응답 DTO")
public class ViewCouponsResponse {
    @ApiModelProperty(value="고객 쿠폰 식별값 필드", dataType = "Long")
    private Long buyerCouponId;
    @ApiModelProperty(value="쿠폰 식별값 필드", dataType = "Long")
    private Long couponId;
    @ApiModelProperty(value="쿠폰 코드 필드", dataType = "String")
    private String couponCode;
    @ApiModelProperty(value="쿠폰 할인 금액 필드", dataType = "Integer")
    private Integer discountPrice;
    @ApiModelProperty(value="쿠폰 등록 시점", dataType = "Instant")
    private Instant createdAt;

    public static ViewCouponsResponse from(BuyerCoupon buyerCoupon) {
        return ViewCouponsResponse.builder()
                .buyerCouponId(buyerCoupon.getId())
                .couponId(buyerCoupon.getCoupon().getId())
                .couponCode(buyerCoupon.getCoupon().getCode())
                .discountPrice(buyerCoupon.getCoupon().getDiscountPrice())
                .createdAt(buyerCoupon.getCreatedAt())
                .build();
    }

}

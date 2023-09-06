package com.supercoding.hanyipman.dto.coupon.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.supercoding.hanyipman.entity.BuyerCoupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ViewCouponsResponse {
    private Long buyerCouponId;
    private Long couponId;
    private String couponCode;
    private Integer discountPrice;
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

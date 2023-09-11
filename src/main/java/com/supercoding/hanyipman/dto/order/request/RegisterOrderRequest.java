package com.supercoding.hanyipman.dto.order.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterOrderRequest {
    private Long buyerCouponId;
    private Long addressId;
}

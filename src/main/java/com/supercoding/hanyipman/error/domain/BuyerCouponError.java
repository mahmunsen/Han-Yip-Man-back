package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BuyerCouponError implements ErrorCode {
    NOT_FOUND_BUYER_COUPON(HttpStatus.BAD_REQUEST.value(), "해당 쿠폰이 존재하지 않습니다.");

    private final int code;
    private final String message;
}

package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CouponErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 유저 찾을 수 없습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 쿠폰 코드에 맞는 쿠폰이 존재하지 않습니다."),

    REGISTERED_BEFORE(HttpStatus.CONFLICT.value(), "해당 쿠폰은 이미 등록된 적이 있어, 등록이 불가합니다.");

    private final int code;
    private final String message;
}

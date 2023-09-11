package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_MIN_PRICE(HttpStatus.BAD_REQUEST.value(), "최소 금액 이상으로 주문해야 합니다.");

    private final int code;
    private final String message;
}

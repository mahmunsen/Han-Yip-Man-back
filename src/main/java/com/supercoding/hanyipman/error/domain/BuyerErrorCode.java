package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BuyerErrorCode implements ErrorCode {
    NOT_BUYER(HttpStatus.FORBIDDEN.value(), "구매자 회원이 아닙니다.");


    private final int code;
    private final String message;

    private BuyerErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}

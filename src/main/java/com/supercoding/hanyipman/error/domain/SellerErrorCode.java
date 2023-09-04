package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SellerErrorCode implements ErrorCode {

    NOT_SELLER(HttpStatus.FORBIDDEN.value(), "사장님 회원이 아닙니다.");


    private final int code;
    private final String message;

    private SellerErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}

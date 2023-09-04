package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ShopErrorCode implements ErrorCode {

    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND.value(), "카테고리가 존재하지 않습니다.");


    private final int code;
    private final String message;

    private ShopErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

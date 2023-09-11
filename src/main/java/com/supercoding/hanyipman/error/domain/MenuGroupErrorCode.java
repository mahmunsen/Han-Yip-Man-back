package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MenuGroupErrorCode implements ErrorCode {

    NOT_FOUND_MENU_GROUP(HttpStatus.NOT_FOUND.value(), "존재하지 않는 메뉴 대분류입니다");


    private final int code;
    private final String message;

    private MenuGroupErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}

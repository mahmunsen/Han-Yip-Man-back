package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UtilErrorCode implements ErrorCode {

    NOT_FOUND_FILE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "파일이 존재하지 않습니다.");


    private final int code;
    private final String message;

    private UtilErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}

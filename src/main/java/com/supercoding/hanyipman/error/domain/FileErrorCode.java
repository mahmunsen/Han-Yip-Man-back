package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FileErrorCode implements ErrorCode {

    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST.value(), "파일 업로드 실패");


    private final int code;
    private final String message;

    private FileErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}

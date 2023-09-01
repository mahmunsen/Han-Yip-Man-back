package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {

    // HTTP 상태 코드 400
    INVALID_MEMBER_ID(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 회원 ID입니다."),

    // HTTP 상태 코드 401
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "잘못된 비밀번호입니다."),

    // HTTP 상태 코드 403
    DUPLICATE_MEMBER_ID(HttpStatus.FORBIDDEN.value(), "이미 사용 중인 회원 ID입니다."),

    // HTTP 상태 코드 404
    NON_EXISTENT_MEMBER(HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다.");

    private final int code;
    private final String message;

    private UserErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(code);
    }
}
package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor

public enum TokenErrorCode implements ErrorCode {

    // HTTP 상태 코드 401
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다."),

    // 문자열을 Long으로 변환할 수 없는 경우 에러
    CANNOT_CONVERT_TO_LONG(HttpStatus.BAD_REQUEST.value(), "문자열을 Long으로 변환할 수 없습니다."),

    // HTTP 상태 코드 401
    INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED.value(), "잘못된 JWT 서명입니다."),

    // HTTP 상태 코드 401
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "만료된 JWT 토큰입니다."),

    // HTTP 상태 코드 401
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "지원되지 않는 JWT 토큰입니다."),

    // HTTP 상태 코드 401
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "JWT 토큰이 잘못되었습니다."),

    // HTTP 상태 코드 403 (Forbidden)
    ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "로그인 후 이용하실 수 있습니다."),

    ;

    private final int code;
    private final String message;


}

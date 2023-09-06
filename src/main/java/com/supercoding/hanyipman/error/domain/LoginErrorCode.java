package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum LoginErrorCode implements ErrorCode {

    // 로그인 정보가 맞지 않는 경우의 오류 코드
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED.value(), "로그인 정보가 올바르지 않습니다.");


    private final int code;
    private final String message;
}

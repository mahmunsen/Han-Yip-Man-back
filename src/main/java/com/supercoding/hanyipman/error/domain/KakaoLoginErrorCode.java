package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum KakaoLoginErrorCode implements ErrorCode {

    // HTTP 상태 코드 400 (유효하지 않은 카카오 로그인 코드)
    INVALID_KAKAO_LOGIN_CODE(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 카카오 로그인 코드입니다."),


    // HTTP 상태 코드 404 (존재하지 않는 회원)
    NON_EXISTENT_MEMBER(HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),
    ;
    private final int code;
    private final String message;

    private KakaoLoginErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}

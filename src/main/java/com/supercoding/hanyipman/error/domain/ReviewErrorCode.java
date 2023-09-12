package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode{

    NOT_PROPER_USER(HttpStatus.FORBIDDEN.value(), "해당 유저는 리뷰 작성이 불가합니다."),

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "가게가 존재하지 않습니다."),

    REGISTERED_BEFORE(HttpStatus.CONFLICT.value(), "해당 주문에 대해 이미 리뷰를 작성하였습니다.");

    private final int code;
    private final String message;
}


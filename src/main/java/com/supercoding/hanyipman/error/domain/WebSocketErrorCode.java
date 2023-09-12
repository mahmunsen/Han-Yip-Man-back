package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum WebSocketErrorCode implements ErrorCode {
    ORDER_NOT_EXIST(HttpStatus.NOT_FOUND.value(), "해당 주문이 존재하지 않습니다.");


    private final int code;
    private final String message;
}

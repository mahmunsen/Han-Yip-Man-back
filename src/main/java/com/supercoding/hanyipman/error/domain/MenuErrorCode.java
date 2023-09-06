package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MenuErrorCode implements ErrorCode {
    NOT_FOUND_MENU(HttpStatus.NOT_FOUND.value(), "요청하신 메뉴를 찾을 수 없습니다.");

    private final int code;
    private final String message;

}

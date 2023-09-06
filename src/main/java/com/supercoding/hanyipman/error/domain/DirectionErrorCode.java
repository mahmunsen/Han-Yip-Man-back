package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DirectionErrorCode implements ErrorCode {
    NOT_FOUND_DIRECTION(HttpStatus.BAD_REQUEST.value(), "정렬 방향은 DESC 혹은 ASC 입니다.");

    private final int code;
    private final String message;
}

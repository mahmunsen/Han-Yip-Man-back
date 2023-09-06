package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OptionErrorCode implements ErrorCode {

    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "옵션을 찾을 수 없습니다."),
    NOT_MATCH_OPTION_AMOUNT(HttpStatus.BAD_REQUEST.value(), "찾은 옵션과 전송받은 옵션의 갯수가 맞지 않습니다.");

    private final int code;
    private final String message;
}

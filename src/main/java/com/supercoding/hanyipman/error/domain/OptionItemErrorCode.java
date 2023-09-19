package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OptionItemErrorCode implements ErrorCode {


    NOT_FOUND_OPTION_ITEM(HttpStatus.NOT_FOUND.value(), "옵션 아이템을 찾을 수 없습니다");

        private final int code;
        private final String message;

}

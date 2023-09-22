package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AddressErrorCode implements ErrorCode {

    EMPTY_ADDRESS_DATA(HttpStatus.BAD_REQUEST.value(), "mapId 데이터가 비어 있습니다."),

    ADDRESS_DATA_EXCEED_LIMIT(HttpStatus.BAD_REQUEST.value(), "주소 데이터는 최소 1개 이상이여야만 합니다."),

    DUPLICATE_ADDRESS(HttpStatus.CONFLICT.value(), "이미 등록되어 있는 주소입니다. 다른 주소를 입력해주세요."),

    UNCHANGEABLE_ADDRESS(HttpStatus.BAD_REQUEST.value(), "변경할 수 없는 주소입니다."),

    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "삭제하려는 주소를 찾을 수 없습니다."),

    MY_ADDRESS_ONLY(HttpStatus.BAD_REQUEST.value(), "나의 주소만 기본 주소로 바꿀 수 있습니다.");

    ;
    private final int code;
    private final String message;

    private AddressErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}

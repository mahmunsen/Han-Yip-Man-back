package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ShopErrorCode implements ErrorCode {

    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND.value(), "카테고리가 존재하지 않습니다."),
    NOT_FOUND_SORT_TYPE(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 정렬 타입입니다."),
    NOT_FOUND_SHOP(HttpStatus.NOT_FOUND.value(), "존재하지 않는 가게 입니다."),
    DIFFERENT_SHOP(HttpStatus.BAD_REQUEST.value(), "장바구니에 담으려는 음식과 기존 장바구니에 담긴 음식의 가게가 다릅니다."),
    DIFFERENT_SELLER(HttpStatus.FORBIDDEN.value(), "내가 등록한 가게만 삭제및 수정할 수 있습니다");



    private final int code;
    private final String message;

    private ShopErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

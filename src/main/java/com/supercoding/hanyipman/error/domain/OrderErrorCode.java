package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_MIN_PRICE(HttpStatus.BAD_REQUEST.value(), "최소 금액 이상으로 주문해야 합니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "주문건이 존재하지 않습니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.BAD_REQUEST.value(), "이미 주문이 취소 되었습니다."),
    NOT_SAME_ORDER_BUYER(HttpStatus.BAD_REQUEST.value(), "요청하신 구매자와 주문하신 구매자가 다릅니다"),
    NOT_SAME_ORDER_SELLER(HttpStatus.BAD_REQUEST.value(), "요청하신 판매자와 가게 사장님과 다릅니다"), 
    NOT_SAME_SHOP_SELLER(HttpStatus.BAD_REQUEST.value(), "가게 판매자와 취소를 요청한 판매자가 다릅니다."),
    INVALID_CHANGE_ORDER_STATUS(HttpStatus.BAD_REQUEST.value(), "주문 상태를 변경할 수 없습니다.");

    private final int code;
    private final String message;
}

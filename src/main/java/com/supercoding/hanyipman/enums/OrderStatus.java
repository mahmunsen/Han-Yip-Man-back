package com.supercoding.hanyipman.enums;

import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.OrderErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    WAIT("WAIT"),
    PAID("PAID"),
    CANCELED("CANCELED"),
    TAKEOVER("TAKEOVER"),
    DELIVERY("DELIVERY"),
    COMPLETE("COMPLETE");

    private final String status;

    public static void isPossibleCancel(OrderStatus orderStatus) {
        if(orderStatus.equals(CANCELED)) throw new CustomException(OrderErrorCode.ORDER_ALREADY_CANCELED);
        if(orderStatus.ordinal() > CANCELED.ordinal()) throw new CustomException(OrderErrorCode.INVALID_CHANGE_ORDER_STATUS);
    }
    public static void isEqualCancel(OrderStatus orderStatus) {
        if(orderStatus.equals(CANCELED)) throw new CustomException(OrderErrorCode.ORDER_ALREADY_CANCELED);
    }
}

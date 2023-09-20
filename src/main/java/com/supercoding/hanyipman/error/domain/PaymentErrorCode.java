package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode implements ErrorCode {

    // 공통
    PAYMENT_COMMON_ALREADY_PAID(HttpStatus.BAD_REQUEST.value(), "이미 결제된 주문건 입니다."),
    PAYMENT_COMMON_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST.value(), "이미 취소된 주문건 입니다."),
    PAYMENT_COMMON_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 주문건에 대한 결제건이 존재하지 않습니다."),
    PAYMENT_COMMON_ALREADY_TAKEOVER(HttpStatus.BAD_REQUEST.value(), "이미 접수된 주문건입니다."),
    PAYMENT_COMMON_ALREADY_COOKING(HttpStatus.BAD_REQUEST.value(), "이미 조리중인 주문건입니다."),
    PAYMENT_COMMON_ALREADY_IN_DELIVERY(HttpStatus.BAD_REQUEST.value(), "배달중인 주문건입니다."),
    PAYMENT_COMMON_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST.value(), "이미 완료된 주문건입니다."),
    PAYMENT_COMMON_NOT_BUYER(HttpStatus.FORBIDDEN.value(), "소비자가 아닙니다."),
    PAYMENT_COMMON_NOT_FOUND_SELLER(HttpStatus.NOT_FOUND.value(), "판매자를 찾을 수 없습니다."),
    PAYMENT_COMMON_NO_ORDER(HttpStatus.NOT_FOUND.value(), "결제할 주문건이 존재하지 않습니다."),
    PAYMENT_COMMON_MISMATCH_ORDER_AND_BUYER(HttpStatus.BAD_REQUEST.value(), "주문건의 소비자와 결제건의 소비자가 일치하지 않습니다."),
    PAYMENT_COMMON_NOT_FOUND_PROVIDER(HttpStatus.BAD_REQUEST.value(), "해당 결제 수단이 존재하지 않습니다."),

    // 아임포트
    IM_PORT_NON_EXISTENT_MEMBER(HttpStatus.NOT_FOUND.value(), "존재하지 않는 회원입니다."),
    IM_PORT_MISMATCH_IMP_UID(HttpStatus.BAD_REQUEST.value(), "Imp_uid 가 맞지 않습니다."),
    IM_PORT_MISMATCH_MERCHANT_UID(HttpStatus.BAD_REQUEST.value(), "Merchant_uid 가 맞지 않습니다."),
    IM_PORT_API_COMMUNICATION_ERROR(HttpStatus.BAD_REQUEST.value(), "아임포트 통신에 실패했습니다."),
    IM_PORT_API_PAYMENT_FAILED(HttpStatus.BAD_REQUEST.value(), "결제에 실패하였습니다."),

    // 카카오페이
    KAKAOPAY_API_COMMUNICATION_ERROR(HttpStatus.BAD_REQUEST.value(), "카카오페이 통신에 실패했습니다.");



    private final int code;
    private final String message;

    private PaymentErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

}

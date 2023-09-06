package com.supercoding.hanyipman.dto.payment.response;

import com.supercoding.hanyipman.entity.Payment;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostPaymentResponse {

    private String merchant_uid;
    private Integer amount;


    static public PostPaymentResponse toDto(Payment savedPayment) {
        return PostPaymentResponse.builder()
                .merchant_uid(savedPayment.getMerchantUid())
                .amount(savedPayment.getTotalAmount())
                .build();

    }
}

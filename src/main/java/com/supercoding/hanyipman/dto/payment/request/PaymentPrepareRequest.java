package com.supercoding.hanyipman.dto.payment.request;
import com.supercoding.hanyipman.entity.OrderTest;
import lombok.*;

@Builder
@Data
public class PaymentPrepareRequest {

    private Integer amount;

    public static PaymentPrepareRequest toDto(OrderTest orderTest) {
        return PaymentPrepareRequest.builder()
                .amount(orderTest.getTotalPrice())
                .build();
    }
}

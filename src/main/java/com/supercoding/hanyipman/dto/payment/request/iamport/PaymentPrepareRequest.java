package com.supercoding.hanyipman.dto.payment.request.iamport;
import com.supercoding.hanyipman.entity.Order;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPrepareRequest {

    private Integer amount;

    public static PaymentPrepareRequest toDto(Order order) {
        return PaymentPrepareRequest.builder()
                .amount(order.getTotalPrice())
                .build();
    }
}

package com.supercoding.hanyipman.dto.payment.request.iamport;
import com.supercoding.hanyipman.entity.OrderTest;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPrepareRequest {

    private Integer amount;

    public static PaymentPrepareRequest toDto(OrderTest orderTest) {
        return PaymentPrepareRequest.builder()
                .amount(orderTest.getTotalPrice())
                .build();
    }
}

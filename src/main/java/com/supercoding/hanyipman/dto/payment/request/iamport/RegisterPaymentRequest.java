package com.supercoding.hanyipman.dto.payment.request.iamport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPaymentRequest {
    private Long orderId;
}

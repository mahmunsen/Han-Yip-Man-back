package com.supercoding.hanyipman.dto.payment.request.iamport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelPaymentRequest {

    private String imp_uid;
    private String merchant_uid;
    private Long orderId;

}

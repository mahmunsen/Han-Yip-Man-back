package com.supercoding.hanyipman.dto.payment.request;

import lombok.Data;

@Data
public class CancelPaymentRequest {

    private String imp_uid;
    private String merchant_uid;

}

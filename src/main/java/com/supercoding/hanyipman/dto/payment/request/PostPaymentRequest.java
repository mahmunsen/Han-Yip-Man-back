package com.supercoding.hanyipman.dto.payment.request;

import lombok.Data;

@Data
public class PostPaymentRequest {

    private String merchant_uid;
    private String imp_uid;

}

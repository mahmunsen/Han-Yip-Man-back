package com.supercoding.hanyipman.dto.payment.response.iamport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOnePaymentResponse {

    private Long code;
    private String message;
    private Response response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Response {

        String imp_uid;
        String merchant_uid;
        Integer amount;
        String pay_method;
        String pg_provider;
        String paid_at;
        String status;

    }

}

package com.supercoding.hanyipman.dto.payment.response.iamport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelPaymentResponse {
    private Long code;
    private String message;
    private Response response;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Response {
        private String imp_uid;
        private String merchant_uid;
        private String pay_method;
        private String pg_provider;
        private String name;
        private Integer amount;
        private String status;
        private Instant cancelled_at;

    }
}

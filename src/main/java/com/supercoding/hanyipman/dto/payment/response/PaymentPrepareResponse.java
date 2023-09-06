package com.supercoding.hanyipman.dto.payment.response;
import lombok.Data;

@Data
public class PaymentPrepareResponse {

    private Long code;
    private String message;
    private Response response;


    @Data
    public class Response {

        String merchant_uid;
        Integer amount;
    }
}

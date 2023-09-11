package com.supercoding.hanyipman.dto.payment.response.iamport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPrepareResponse {

    private Long code;
    private String message;
    private Response response;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Response {

        String merchant_uid;
        Integer amount;
    }
}

package com.supercoding.hanyipman.dto.payment.request.iamport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostPaymentRequest {

    private String merchant_uid;
    private String imp_uid;
    private Long orderId;

}

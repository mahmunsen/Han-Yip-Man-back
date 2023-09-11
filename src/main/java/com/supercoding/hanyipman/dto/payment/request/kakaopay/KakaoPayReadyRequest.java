package com.supercoding.hanyipman.dto.payment.request.kakaopay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPayReadyRequest {

    private Long orderId;
}

package com.supercoding.hanyipman.dto.payment.request.kakaopay;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPayCancelRequest {

    private Long orderId; // 주문 번호

}

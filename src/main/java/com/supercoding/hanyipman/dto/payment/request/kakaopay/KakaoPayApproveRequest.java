package com.supercoding.hanyipman.dto.payment.request.kakaopay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPayApproveRequest {

    private String tid;   // 결제 고유번호
    private String merchant_uid;   // merchant_uid
    private String pg_token;  // 결제승인 요청을 인증하는 토큰, 사용자 결제 수단 선택 완료 시, approval_url로 redirection해줄 때 pg_token을 query string으로 전달
    private Long orderId;

}

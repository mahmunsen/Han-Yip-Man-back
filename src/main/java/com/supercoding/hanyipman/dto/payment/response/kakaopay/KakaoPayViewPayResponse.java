package com.supercoding.hanyipman.dto.payment.response.kakaopay;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPayViewPayResponse {

    private String tid; // 결제 고유 번호
    private String cid; // 가맹점 코드
    private String status; // 결제 상태
    private String partner_order_id; // 가맹점 주문번호
    private String partner_user_id;  // 가맹점 회원 아이디
    private String payment_method_type; // 결제 수단
    private Amount amount; // 결제 금액 정보
    private CanceledAmount canceled_amount; // 취소된 금액
    private String item_name; // 상품 이름
    private Integer quantity; // 상품 수량
    private Date created_at;  // 결제 준비 요청 시각
    private Date approved_at; // 결제 승인 시각
    private Date canceled_at; // 결제 승인 시각


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Amount {
        private Integer total; // 전체 결제 금액
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class CanceledAmount {
        private Integer total; // 전체 취소 금액

    }
}

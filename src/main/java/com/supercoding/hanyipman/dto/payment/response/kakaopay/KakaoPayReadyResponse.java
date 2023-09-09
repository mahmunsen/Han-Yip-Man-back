package com.supercoding.hanyipman.dto.payment.response.kakaopay;
import lombok.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoPayReadyResponse {

    // 결제고유번호
    private String tid;
    // 요청한 클라이언트가 PC 웹일 경우 카카오톡으로 결제 요청 메시지(TMS)를 보내기 위한 사용자 정보 입력 화면
    private String next_redirect_pc_url;
    private Date created_at;
    private String merchant_uid;

}

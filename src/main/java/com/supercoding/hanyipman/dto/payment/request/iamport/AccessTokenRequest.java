package com.supercoding.hanyipman.dto.payment.request.iamport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenRequest {

    private Long orderId;

}

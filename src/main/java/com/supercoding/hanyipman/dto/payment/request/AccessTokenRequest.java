package com.supercoding.hanyipman.dto.payment.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenRequest {

    private String apikey;
    private String apiSecret;

}

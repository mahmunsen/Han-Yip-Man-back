package com.supercoding.hanyipman.dto.payment.response.iamport;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse {

    private Long code;
    private String message;
    private Response response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Response {

        private String access_token;
        private long expired_at;
        private long now;

    }
}

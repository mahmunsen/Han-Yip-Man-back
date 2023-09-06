package com.supercoding.hanyipman.dto.payment.response;

import lombok.Data;

@Data
public class AccessTokenResponse {

    private Long code;
    private String message;
    private Response response;

    @Data
    public class Response {

        private String access_token;
        private long expired_at;
        private long now;

    }
}

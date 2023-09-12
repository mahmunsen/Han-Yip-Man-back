package com.supercoding.hanyipman.dto.websocket;

import lombok.Data;

@Data
public class ErrorMessage {

    private String status;
    private String errorMessage;

    public ErrorMessage(String status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

}

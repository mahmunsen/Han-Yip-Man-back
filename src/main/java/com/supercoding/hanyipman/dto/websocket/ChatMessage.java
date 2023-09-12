package com.supercoding.hanyipman.dto.websocket;

import lombok.Data;

@Data
public class ChatMessage {
    private MessageType type;
    private String message;
    private String room;

    public ChatMessage() {
    }

    public ChatMessage(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

}
package com.supercoding.hanyipman.dto.websocket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ChatMessage {
    private MessageType type;
    private String message;
    private String room;

    public ChatMessage(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

}
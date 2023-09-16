package com.supercoding.hanyipman.error.domain;

import com.supercoding.hanyipman.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SseErrorCode implements ErrorCode {
    NOT_FOUND_EMITTER(HttpStatus.NOT_FOUND.value(), "유저와 관련되 SSE를 찾을 수 없습니다."),
    CANT_SEND_MESSAGE(500, "이벤트관련 메시지를 전송할 수 없습니다.");

    private final int code;
    private final String message;
}

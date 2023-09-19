package com.supercoding.hanyipman.dto.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendSseResponse<T> {
    private Long userId;
    @Setter private Long sendCount;
    private T data;
    public static <T> SendSseResponse<T> of(Long userId, T data){
        return new SendSseResponse<>(userId, 0L, data);
    }

    public boolean isValidCount(){
        sendCount += 1;
        return sendCount <= 5;
    }
}

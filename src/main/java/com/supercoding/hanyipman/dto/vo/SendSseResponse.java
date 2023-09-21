package com.supercoding.hanyipman.dto.vo;

import com.supercoding.hanyipman.enums.EventName;
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
    private String eventName;

    public static <T> SendSseResponse<T> of(Long userId, T data){
        return new SendSseResponse<>(userId, 0L, data, null);
    }

    public static <T> SendSseResponse<T> of(Long userId, T data, String name){
        return new SendSseResponse<>(userId, 0L, data, name);
    }

    public boolean isValidCount(){
        sendCount += 1;
        return sendCount <= 5;
    }
}

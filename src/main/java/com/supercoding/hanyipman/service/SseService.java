package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.enums.EventName;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.SseErrorCode;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.repository.SseRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Service
public class SseService {
    private final SseRepository sseRepository;
    private final UserRepository userRepository;
    private final Long timeOut = 10 * 10 * 1000L; // 10분

    public SseEmitter registerEmitter(Long userId){
        userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
        SseEmitter sseEmitter = generateSse(userId);
        sseRepository.save(userId, sseEmitter);
        return sseEmitter;
    }

    private SseEmitter generateSse(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(timeOut);
        sseEmitter.onTimeout(() -> {
            log.info("호출: sse 타임아웃");
            sseEmitter.complete();
        });
        sseEmitter.onCompletion(() -> {
            log.info("호출: sse 완료");
            sseEmitter.complete();
            sseRepository.delete(userId);
        });
        sendMessage(EventName.SUBSCRIBE, "hello", sseEmitter);

        return sseEmitter;
    }

    public <T> void validSendMessage(Long userId, EventName eventName, T data) {
        SseEmitter emitter = sseRepository.findEmitterByUserId(userId).orElseThrow(() -> new CustomException(SseErrorCode.NOT_FOUND_EMITTER));
        sendMessage(eventName, data, emitter);
    }

    private <T> void sendMessage(EventName eventName, T data, SseEmitter emitter) {
        try{
            emitter.send(SseEmitter.event()
                            .name(eventName.getEventName())
                            .data(data)
            );
        }catch(IOException e) {
            throw new CustomException(SseErrorCode.CANT_SEND_MESSAGE);
        }
    }
}

package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.vo.SendSseResponse;
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
public class SseEventService {
    private final SseRepository sseRepository;
    private final UserRepository userRepository;
    private final Long timeOut = 10 *  60 * 1000L; // 10분

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
        try {
            SseEmitter emitter = findUserByUserId(userId);
            sendMessage(eventName, data, emitter);
        }catch(CustomException e) {
            log.error("SSE 알림을 실행시키지 못했습니다.");
        }
    }

    public SseEmitter findUserByUserId(Long userId) {
        return sseRepository.findEmitterByUserId(userId).orElseThrow(() -> new CustomException(SseErrorCode.NOT_FOUND_EMITTER));
    }

    public <T> void sendMessage(EventName eventName, T data, SseEmitter emitter) {
        try{
            emitter.send(SseEmitter.event()
                            .name(eventName.getEventName())
                            .data(data)
            );
        }catch(IOException e) { //TODO: 메시지 전송 비동기 처리 고려
            log.info("SSE 알림을 실행시키지 못했습니다.");
            sseRepository.addSendResponse(new SendSseResponse<>());
//            throw new CustomException(SseErrorCode.CANT_SEND_MESSAGE);
        }
    }
}

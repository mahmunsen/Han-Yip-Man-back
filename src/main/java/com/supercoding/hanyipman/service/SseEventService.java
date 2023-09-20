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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Service
public class SseEventService {
    private final SseRepository sseRepository;
    private final UserRepository userRepository;
    private final Long timeOut = 45 * 1000L;

    public SseEmitter registerEmitter(Long userId){
        userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
        SseEmitter sseEmitter = generateSse(userId);
        sseRepository.save(userId, sseEmitter);
        return sseEmitter;
    }

    private SseEmitter generateSse(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(timeOut);
        sseEmitter.onTimeout(sseEmitter::complete);
        sseEmitter.onCompletion(() -> {
            log.info("호출: sse 완료");
            sseRepository.delete(userId);
        });
        sendMessage(userId, EventName.SUBSCRIBE, "연결", sseEmitter);

        return sseEmitter;
    }

    public <T> void validSendMessage(Long userId, EventName eventName, T data) {
        try {
            SseEmitter emitter = findSseByUserId(userId);
            sendMessage(userId, eventName, data, emitter);
        }catch(CustomException e) {
            log.error("SSE 알림을 실행시키지 못했습니다.");
        }
    }

    @Scheduled( cron = "0 * * * * *")
    public void resendSse(){
        log.info("Sse Scheduling");
        sseRepository.findAllSendResponseAndClear().forEach((send) -> {
            SseEmitter sse = findSseByUserId(send.getUserId());
            sendMessage(send.getUserId(), send.getEventName(), send.getData(), sse);
        });
    }

    public SseEmitter findSseByUserId(Long userId) {
        return sseRepository.findEmitterByUserId(userId).orElseThrow(() -> new CustomException(SseErrorCode.NOT_FOUND_EMITTER));
    }

    private <T> void sendMessage(Long userId, EventName eventName, T data, SseEmitter emitter) {
        try{
            emitter.send(SseEmitter.event()
                            .name(eventName.getEventName())
                            .data(data)
            );
        }catch(IOException e) { //TODO: 메시지 전송 비동기 처리 고려
            log.info("SSE 알림을 실행시키지 못했습니다.");
            sseRepository.addSendResponse(SendSseResponse.of(userId, emitter, eventName));
        }
    }
}

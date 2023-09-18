package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.vo.SendSseResponse;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.repository.SseRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseMessageService {
    private final SseRepository sseRepository;
    private final UserRepository userRepository;
    private final Long timeOut = 10 *  60 * 1000L; // 10분

    public SseEmitter registerSse(Long userId){
        findUserByUserId(userId);
        SseEmitter sseEmitter = generateSse(userId);
        sseRepository.save(userId, sseEmitter);
        return sseEmitter;
    }


    public void sendSse(SendSseResponse<?> sendSseResponse) {
        sseRepository.findEmitterByUserId(sendSseResponse.getUserId()).ifPresent(sse -> {
            try {
                sse.send(sendSseResponse.getData());
            } catch (IOException e) {
                log.error("sendSse(): SSE 알림을 실행시키지 못했습니다.");
                if(sendSseResponse.isValidCount()) sseRepository.addSendResponse(sendSseResponse);
            }
        });
    }

    @Scheduled( cron = "0 * * * * *")
    public void resendSse(){
        log.info("Sse Scheduling");
        sseRepository.findAllSendResponseAndClear().forEach(this::sendSse);
    }

//    @Scheduled( cron = "0 * * * * *")
//    public void checkTimeoutSse(){
//        sseRepository.checkTimeoutSse();
//    }


    private Optional<SseEmitter> findSseByUserId(Long userId) {
        return sseRepository.findEmitterByUserId(userId);
    }
    private User findUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
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
        sendSse(SendSseResponse.of(userId, "연결"));

        return sseEmitter;
    }
}

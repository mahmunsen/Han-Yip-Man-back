package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.dto.vo.SendSseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@RequiredArgsConstructor
@Repository
public class SseRepository {

    private final Map<Long, SseEmitter> sses = new ConcurrentHashMap<>();
    private Deque<SendSseResponse<?>> sseResponses = new ConcurrentLinkedDeque<>();

    public SseEmitter save(Long userId, SseEmitter emitter) {
        sses.put(userId, emitter);
        return emitter;
    }
    public Optional<SseEmitter> findEmitterByUserId(Long userId) {
        return Optional.ofNullable(sses.get(userId));
    }
    public void delete(Long userId){
        sses.remove(userId);
    }

    public void addSendResponse(SendSseResponse<?> objectSendSseResponse) {
        sseResponses.add(objectSendSseResponse);
    }

    public Deque<SendSseResponse<?>> findAllSendResponseAndClear() {
        Deque<SendSseResponse<?>> returnResponse = sseResponses;
        sseResponses = new ConcurrentLinkedDeque<>();
        return returnResponse;
    }

//    public void checkTimeoutSse() {
//        sses.entrySet().forEach(entry -> {
//            entry.getValue()
//        });
//    }
}

package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Repository
public class SseRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        return emitter;
    }
    public Optional<SseEmitter> findEmitterByUserId(Long userId) {
        return Optional.ofNullable(emitters.get(userId));
    }
    public void delete(Long userId){
        emitters.remove(userId);
    }

}

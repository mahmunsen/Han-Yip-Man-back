package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.vo.SendSseResponse;
import com.supercoding.hanyipman.enums.EventName;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.repository.SseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SSE 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class SseServiceTest {

    @InjectMocks
    private SseMessageService sseService;
    @Mock
    private SseRepository sseRepository;


    @Nested
    @DisplayName("validSendMessage()")
    class ValidSendMessage {
        @Test
        void SSE_메시지_전송() {
            //given
            Long userId = 1L;

            //when

            when(sseRepository.findEmitterByUserId(userId)).thenReturn(Optional.ofNullable(mock(SseEmitter.class)));

            //then
            assertDoesNotThrow(() -> sseService.sendSse(SendSseResponse.of(userId, null)));
        }
    }


    @Test
    void 동시성_테스트코드() throws InterruptedException {
        DequeTest list1 = new DequeTest();
        for(int i = 0; i < 10; i++) {
            int finalI = i;
            Thread th1 = new Thread(() ->
            {
                Deque<String> lists = list1.setLists();
                System.out.println(finalI + " 초기화: " + lists.size());
            });
            int finalI1 = i;
            Thread th2 = new Thread(() ->
            {
                for(int j = 0; j < 10_000; j++) {
                    list1.getLists().add(String.valueOf(j));
                }
                System.out.println(finalI1 + " 더하기: " + list1.getLists().size());
            });
            th1.start();
            Thread.sleep(100L);
            th2.start();
            Thread.sleep(100L);
        }
    }

    class DequeTest {
        public Deque<String> lists = new ConcurrentLinkedDeque<>(Arrays.asList("arr1", "arr2"));

        public Deque<String> getLists() {
            return lists;
        }
        public Deque<String> setLists() {
            Deque<String> newLists = lists;
            lists = new ConcurrentLinkedDeque<>();
            return newLists;
        }
    }

}
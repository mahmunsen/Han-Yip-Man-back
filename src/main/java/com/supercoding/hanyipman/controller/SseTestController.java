package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.sse.SseTestRequest;
import com.supercoding.hanyipman.dto.sse.SseTestResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.dto.vo.SendSseResponse;
import com.supercoding.hanyipman.enums.EventName;
import com.supercoding.hanyipman.service.SseEventService;
import com.supercoding.hanyipman.service.SseMessageService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Api(tags = "Sse")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseTestController {
    private final SseEventService sseService;

    @Operation(summary = "서버 구독하기", description = "Sse 알림을 받기 위한 서버 구독URL")
    @GetMapping(headers = "X-API-VERSION=1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter registerEmitter(@AuthenticationPrincipal CustomUserDetail auth)  {
        log.info("Sse 구독");
        return sseService.registerEmitter(auth.getUserId());
    }


    @Operation(summary = "서버 알림 테스트 URL", description = "Sse 알림을 테스트 하기 위한 URL")
    @PostMapping(headers = "X-API-VERSION=1")
    public Response<?> sendMessage(
            @RequestBody SseTestRequest request,
            @AuthenticationPrincipal CustomUserDetail auth) {
        SendSseResponse<SseTestResponse> send = SendSseResponse.of(auth.getUserId(), new SseTestResponse(request.getTitle(), request.getContent()));
        sseService.validSendMessage(auth.getUserId(), EventName.NOTICE_ORDER, send);
        return ApiUtils.success(HttpStatus.OK, "메시지가 전송됐습니다.", null);
    }
}

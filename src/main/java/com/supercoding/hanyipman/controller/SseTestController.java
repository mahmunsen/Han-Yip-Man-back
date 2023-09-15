package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.sse.SseTestRequest;
import com.supercoding.hanyipman.dto.sse.SseTestResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.enums.EventName;
import com.supercoding.hanyipman.service.SseService;
import com.supercoding.hanyipman.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseTestController {
    private final SseService sseService;

    @GetMapping
    public SseEmitter registerEmitter(@AuthenticationPrincipal CustomUserDetail auth)  {
        return sseService.registerEmitter(auth.getUserId());
    }


    // TODO: sse 메시지 전송 예시 코드
    @PostMapping
    public Response<?> sendMessage(
            @RequestBody SseTestRequest request,
            @AuthenticationPrincipal CustomUserDetail auth) {
        sseService.validSendMessage(auth.getUserId(), EventName.NOTICE_ORDER, new SseTestResponse(request.getTitle(), request.getContent()));
        return ApiUtils.success(HttpStatus.OK, "메시지가 전송됐습니다.", null);
    }
}

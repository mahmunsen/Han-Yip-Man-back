package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.user.response.LoginResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import com.supercoding.hanyipman.utils.ApiUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/token-test")
@Slf4j
public class TokenTestController {

    private final JwtTokenProvider jwtTokenProvider;
    Authentication auth;

    @GetMapping("/parser")
//    @PreAuthorize("hasRole('BUYERS')")
    public Response<Object> tokenTest(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        log.info(customUserDetail.toString());
        Long userId = customUserDetail.getUserId();
        String email = customUserDetail.getEmail();
        List<String> authority = customUserDetail.getAuthority();
        return ApiUtils.success(200, "토큰테스트", customUserDetail);
    }


}

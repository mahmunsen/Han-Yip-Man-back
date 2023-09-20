package com.supercoding.hanyipman.controller;


import com.supercoding.hanyipman.dto.user.response.LoginResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.KakaoOauthService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class KakaoController {

    @Value("${kakao.kakaoClientId}")
    private String kakaoClientId;
    @Value("${kakao.kakaoRedirectUri}")
    private String kakaoRedirectUri;


    private final KakaoOauthService kakaoOauthService;

    @GetMapping("/login/kakao")
    public String kakaoLogin() {
        return "kauth.kakao.com/oauth/authorize?client_id=" + kakaoClientId + "&redirect_uri=" + kakaoRedirectUri + "&response_type=code";
    }


    @Operation(summary = "카카오로그인", description = "인가 코드를 이용해 토큰을 받고, 해당 토큰으로 사용자 정보를 조회합니다. 사용자 정보를 이용하여 서비스에 로그인 및 회원가입합니다.")
    @GetMapping(value = "/kakao/login")
    public Response<LoginResponse> kakaoOauth(@RequestParam("code") String code) {
        LoginResponse loginResponse = kakaoOauthService.kakaoLoginOauth(code);
        return ApiUtils.success(HttpStatus.OK, loginResponse.getNickname() + " 카카오로그인 성공", loginResponse);
    }

}
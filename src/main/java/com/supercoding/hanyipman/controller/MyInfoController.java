package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.myInfo.response.MyInfoResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.TokenErrorCode;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.service.MyInfoService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Api(tags = "마이페이지(유저, 점주)")
public class MyInfoController {

    private final MyInfoService myInfoService;

    @TimeTrace
    @GetMapping(value = "/users/my-info",headers = "X-API-VERSION=1")
    @Operation(summary = "구매자 마이페이지 API", description = "회원의 정보와 회원에 등록어 주소 리스트가 출력됩니다.")
    public Response<MyInfoResponse> buyerUserMyInfo() {
        return ApiUtils.success(HttpStatus.OK, "유저 마이페이지 응답 성공", myInfoService.getUserInfoForMyPage(JwtToken.user()));
    }


}

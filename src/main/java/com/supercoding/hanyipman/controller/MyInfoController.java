package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.myInfo.response.MyInfoResponse;
import com.supercoding.hanyipman.dto.myInfo.request.SellerUpdateInfoRequest;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.service.MyInfoService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Api(tags = "마이페이지(유저, 점주)")
public class MyInfoController {

    private final MyInfoService myInfoService;

    @TimeTrace
    @GetMapping(value = "/users/my-info", headers = "X-API-VERSION=1")
    @Operation(summary = "구매자, 판매자 마이페이지 API", description = "회원은 나의정보 및 등록주소 표시, 사장은 나의정보 포시")
    public Response<MyInfoResponse> buyerUserMyInfo() {
        return ApiUtils.success(HttpStatus.OK, "마이페이지 응답 성공", myInfoService.getUserInfoForMyPage(JwtToken.user()));
    }

    @PatchMapping("/sellers/my-info")
    @Operation(summary = "사장님,마이페이지 수정 API", description = "사장님의 유저정보와 사업자 번호를 수정합니다.")
    public Response<Void> sellerUpdateInfo(@RequestBody SellerUpdateInfoRequest request) {
        myInfoService.sellerUpdateInfo(JwtToken.user(), request);
        return ApiUtils.success(HttpStatus.OK, "마이페이지가 정상적으로 수정되었습니다.", null);
    }


}

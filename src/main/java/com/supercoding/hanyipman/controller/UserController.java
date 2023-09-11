package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import com.supercoding.hanyipman.dto.user.request.LoginRequest;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import com.supercoding.hanyipman.dto.user.response.LoginResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.security.filters.JwtAuthenticationFilter;
import com.supercoding.hanyipman.service.UserService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
@Api(tags = "회원 관리(유저, 점주)")
public class UserController {

    private UserService userService;

    @PostMapping(value = "/users/login", headers = "X-API-VERSION=1")
    @ApiOperation(value = "유저,업주 로그인 API", nickname = "유저,업주 로그인 API")
    public Response<LoginResponse> allLogin(@RequestBody LoginRequest request) {
        User loginUser = userService.login(request);
        LoginResponse loginResponse = userService.tokenGenerator(loginUser);
        // response header 에도 넣고 응답 객체에도 넣는다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.getAccessToken());

        return ApiUtils.success(HttpStatus.OK, "로그인 성공", loginResponse);
    }

    @PostMapping(value = "/sellers/signup", headers = "X-API-VERSION=1")
    @ApiOperation(value = "업주 회원가입 API", nickname = "업주 회원가입 API")
    public Response<Void> sellersSignup(@RequestBody SellerSignUpRequest request) {
        String signupEmail = userService.sellerSignup(request);
        return ApiUtils.success(HttpStatus.CREATED, signupEmail + "업주 등록 성공", null);
    }

    @PostMapping(value = "/buyers/signup", consumes = "multipart/form-data", headers = "X-API-VERSION=1")
    @ApiOperation(value = "유저 회원가입 API", nickname = "유저 회원가입 API")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data",
            schema = @Schema(implementation = MultipartFile.class)))
    public Response<Void> buyersSignup(
            @ModelAttribute BuyerSignUpRequest request,
            @RequestPart(value = "profileImageFile", required = false) MultipartFile profileImageFile) {
        String signupEmail = userService.buyersSignup(request, profileImageFile);
        return ApiUtils.success(HttpStatus.CREATED, signupEmail + "회원가입 성공", null);
    }

    @GetMapping(value = "/users/check-email-duplicate")
    @Operation(summary = "이메일 중복확인", description = "이메일 데이터를 받아 중복이 있는지 확인합니다.")
    public Response<Void> checkDuplicateEmail(String checkEmail) {
        userService.checkDuplicateEmail(checkEmail);

        return ApiUtils.success(HttpStatus.OK, "사용할 수 있는 이메일입니다.", null);
    }
}

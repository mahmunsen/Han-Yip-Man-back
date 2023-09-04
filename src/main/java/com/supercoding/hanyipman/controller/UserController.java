package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import com.supercoding.hanyipman.dto.user.request.LoginRequest;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import com.supercoding.hanyipman.dto.user.response.LoginResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.security.filters.JwtAuthenticationFilter;
import com.supercoding.hanyipman.service.UserService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    @PostMapping(value = "/users/login")
    @ApiOperation(value = "유저,업주 로그인 API", nickname = "유저,업주 로그인 API")
    public Response<LoginResponse> allLogin(@RequestBody LoginRequest request) {
        User loginUser = userService.login(request);
        LoginResponse loginResponse = userService.tokenGenerator(loginUser);
        // response header 에도 넣고 응답 객체에도 넣는다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + loginResponse.getAccessToken());

        return ApiUtils.success(HttpStatus.OK, "로그인 성공", loginResponse);
    }

    @PostMapping(value = "/sellers/signup")
    @ApiOperation(value = "업주 회원가입 API", nickname = "업주 회원가입 API")
    public Response sellersSignup(@RequestBody SellerSignUpRequest request) {
        String signupEmail = userService.sellerSignup(request);
        return ApiUtils.success(HttpStatus.CREATED, signupEmail + "업주 등록 성공", null);
    }

    @PostMapping(value = "/buyers/signup")
    @ApiOperation(value = "유저 회원가입 API", nickname = "유저 회원가입 API")
    public Response buyersSignup(@RequestBody BuyerSignUpRequest request) {
        String signupEmail = userService.buyersSignup(request);
        return ApiUtils.success(HttpStatus.CREATED, signupEmail + "회원가입 성공", null);
    }
}

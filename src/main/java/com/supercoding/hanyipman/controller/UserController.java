package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.auth.request.LoginRequest;
import com.supercoding.hanyipman.dto.auth.request.SellerSignUpRequest;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.UserService;
import com.supercoding.hanyipman.utils.ApiUtils;
import lombok.AllArgsConstructor;
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
    public String allLogin(@RequestBody LoginRequest request) {
        return "login";
    }

    @PostMapping(value = "/sellers/signup")
    public Response sellersSignup(@RequestBody SellerSignUpRequest request) {
        String signupEmail = userService.sellerSignup(request);

        return ApiUtils.success(HttpStatus.CREATED, signupEmail + "가입 성공", null);
    }
}

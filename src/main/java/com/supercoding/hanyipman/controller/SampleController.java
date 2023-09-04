package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.utils.ApiUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample")
public class SampleController {

    @GetMapping("/success")
    public Response<String> successApiSample() {
        return ApiUtils.success(HttpStatus.OK, "응답 성공", "스트링 데이터");
    }

    @GetMapping("/error")
    public Response<String> errorApiSample() {
        throw new CustomException(UserErrorCode.INVALID_PASSWORD);
    }

}

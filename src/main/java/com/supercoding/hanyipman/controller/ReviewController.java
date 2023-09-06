package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.reivew.request.RegisterReviewRequest;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.ReviewService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api("리뷰 관리")
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "리뷰 등록", description = "리뷰와 별점을 등록한다.")
    public Response<?> registerReview(@AuthenticationPrincipal CustomUserDetail userDetail,
                                       RegisterReviewRequest registerReviewRequest

    ) {
        reviewService.registerReview(userDetail, registerReviewRequest);
        return ApiUtils.success(HttpStatus.CREATED, "리뷰 등록이 완료되었습니다.", null);
    }
}

package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.coupon.request.RegisterCouponRequest;
import com.supercoding.hanyipman.dto.coupon.response.ViewCouponsResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.CouponService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@Api("쿠폰 관리")
@RequestMapping("/api/coupons")
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    @Operation(summary = "쿠폰 등록", description = "쿠폰에 맞는 코드를 입력할 경우, 해당 쿠폰이 등록된다.")
    public Response<?> registerCoupon(@RequestBody RegisterCouponRequest request,
                                      @AuthenticationPrincipal CustomUserDetail userDetail){

        couponService.registerCoupon(request, userDetail);

        return ApiUtils.success(HttpStatus.CREATED, request.getCouponCode() + " 쿠폰 등록에 성공했습니다.", null);
    }

    @GetMapping
    @Operation(summary = "등록된 쿠폰 조회", description = "고객이 등록한 쿠폰 목록을 조회합니다.")
    public Response<?> viewCoupons(@AuthenticationPrincipal CustomUserDetail userDetail) {
        List<ViewCouponsResponse> coupons = couponService.viewCoupons(userDetail);

        return ApiUtils.success(HttpStatus.OK, "고객이 등록한 쿠폰 목록 조회에 성공했습니다.", coupons);
    }
}

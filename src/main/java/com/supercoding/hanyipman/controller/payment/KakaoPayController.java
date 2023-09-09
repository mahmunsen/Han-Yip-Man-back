package com.supercoding.hanyipman.controller.payment;

import com.supercoding.hanyipman.dto.payment.request.kakaopay.KakaoPayCancelRequest;
import com.supercoding.hanyipman.dto.payment.request.kakaopay.KakaoPayReadyRequest;
import com.supercoding.hanyipman.dto.payment.response.kakaopay.*;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.service.PaymentService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Api(tags = "결제 API (카카오페이)")
public class KakaoPayController {

    private final PaymentService paymentService;

    @ApiOperation(value = "(카카오페이) 단건결제준비 API ", nickname = "카카오서버에 정보전달하고 결제고유번호(TID) 받는 API")
    @PostMapping(path = "/ready", headers = "X-API-VERSION=1")
    public Response<Object> kakaoPayReady(@RequestBody KakaoPayReadyRequest kakaoPayReadyRequest) {
        Long orderId = kakaoPayReadyRequest.getOrderId();
        KakaoPayReadyResponse kakaoPayReadyResponse = paymentService.kakaopayReady(orderId, JwtToken.user());

        return ApiUtils.success(HttpStatus.OK.value(), "결제준비가 완료되었습니다.", kakaoPayReadyResponse);
    }

    @ApiOperation(value = "(카카오페이) 결제승인요청 API ", nickname = "approve_url")
    @GetMapping(path = "/approve/{order_id}")
    public Response<Object> kakaoPayApprove(@RequestParam String pg_token, @PathVariable(value = "order_id") Long orderId) {
        KakaoPayApproveResponse kakaoPayApproveResponse = paymentService.kakaopayApprove(pg_token, JwtToken.user(), orderId);

        return ApiUtils.success(HttpStatus.OK.value(), "결제가 완료되었습니다.", kakaoPayApproveResponse);
    }

    @ApiOperation(value = "(카카오페이) 결제진행중 취소", nickname = "cancel_url")
    @GetMapping(path = "/kakaoPayCancel/{order_id}")
    public Response<Object> kakaoPayCancel(@PathVariable(value = "order_id") Long orderId) {

        paymentService.kakaoPayCancelOrFail(orderId);

        return ApiUtils.success(HttpStatus.EXPECTATION_FAILED.value(), "사용자가 결제를 취소하였습니다.", null);
    }
    // EXPECTATION_FAILED: 요청 본문 조건 불충족 시 서버가 해당 요청을 처리할 수 없을 때.

    @ApiOperation(value = "(카카오페이) 결제진행중 실패", nickname = "fail_url")
    @GetMapping(path = "/kakaoPayFail/{order_id}")
    public Response<Object> kakaoPayFail(@PathVariable(value = "order_id") Long orderId) {

        paymentService.kakaoPayCancelOrFail(orderId);

        return ApiUtils.success(HttpStatus.EXPECTATION_FAILED.value(), "결제가 실패하였습니다.", null);
    }


    @ApiOperation(value = "(카카오페이) 결제건 조회 API ", nickname = "결제건 상세정보 조회하는 API")
    @GetMapping(path = "/order/{tid}/{orderId}", headers = "X-API-VERSION=1")
    public Response<Object> kakaoPayViewPayment(@PathVariable("tid") String tid, @PathVariable("orderId") Long orderId) {

        KakaoPayViewPayResponse kakaoPayViewPayResponse = paymentService.kakaopayViewOnePayment(tid, orderId, JwtToken.user());

        return ApiUtils.success(HttpStatus.OK.value(), "결제내역 단건 조회에 성공하였습니다.", kakaoPayViewPayResponse);
    }

    @ApiOperation(value = "(카카오페이) 결제승인 후 결제취소 API ", nickname = "사용자가 결제된 건을 취소하는 API")
    @PostMapping(path = "/cancel", headers = "X-API-VERSION=1")
    public Response<Object> AfterkakaoPayCancelPayment(@RequestBody KakaoPayCancelRequest kakaoPayCancelRequest) {

        KakaoPayCancelResponse kakaoPayCancelResponse = paymentService.afterKakaoPayCancel(kakaoPayCancelRequest, JwtToken.user());

        return ApiUtils.success(HttpStatus.OK.value(), "결제가 취소되었습니다. ", kakaoPayCancelResponse);
    }
}

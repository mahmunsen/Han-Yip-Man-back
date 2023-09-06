package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.payment.request.CancelPaymentRequest;
import com.supercoding.hanyipman.dto.payment.request.PaymentPrepareRequest;
import com.supercoding.hanyipman.dto.payment.request.PostPaymentRequest;
import com.supercoding.hanyipman.dto.payment.response.CancelPaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.GetOnePaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.PostPaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.PaymentPrepareResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.PaymentService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Api(tags = "결제 API")
public class PaymentController {

    private final PaymentService paymentService;

    /* todo 엑세스 토큰, 버전 02 (입력폼 없음) */
    @ApiOperation(value = "아임포트 엑세스 토큰 API ", nickname = "아임포트로부터 엑세스 토큰을 발급받는 API")
    @PostMapping(path = "/getToken", headers = "X-API-VERSION=1")
    public Response<Object> getToken(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        String accessToken = paymentService.getToken(customUserDetail);

        return ApiUtils.success(HttpStatus.OK.value(), "엑세스 토큰 발급에 성공했습니다. ", accessToken);
    }

    /* todo 결제사전검증, 버전 02 (토큰 입력 없음) */
    @ApiOperation(value = "결제사전검증 API ", nickname = "아임포트에 결제번호와 결제예정금액을 사전에 등록하는 API")
    @PostMapping(path = "/prepare", headers = "X-API-VERSION=1")
    public Response<PaymentPrepareResponse> paymentPrepare(@AuthenticationPrincipal CustomUserDetail customUserDetail) {

        PaymentPrepareResponse paymentPrepareResponse = paymentService.paymentPrepare(customUserDetail);

        return ApiUtils.success(HttpStatus.OK.value(), "결제사전검증에 성공하였습니다.", paymentPrepareResponse);
    }

    @ApiOperation(value = "결제내역 단건조회 API ", nickname = "아임포트에서 결제내역 단건을 조회 API")
    @GetMapping(path = "/{imp_uid}", headers = "X-API-VERSION=1")
    public Response<GetOnePaymentResponse> getOnePayment(@PathVariable("imp_uid") String imp_uid, @AuthenticationPrincipal CustomUserDetail customUserDetail) {

        GetOnePaymentResponse getOnePaymentResponse = paymentService.paymentInfo(imp_uid, customUserDetail);
        return ApiUtils.success(HttpStatus.OK.value(), "결제내역 단건조회에 성공하였습니다.", getOnePaymentResponse);
    }

    @ApiOperation(value = "결제사후검증 API ", nickname = "(아임포트)실제결제된 금액과 (주문)결제예상금액이 맞는지 확인하는 API")
    @PostMapping(path = "/complete", headers = "X-API-VERSION=1")
    public Response<Object> verifyPayment(@RequestBody PostPaymentRequest postPaymentRequest, @AuthenticationPrincipal CustomUserDetail customUserDetail) {

        ResponseEntity<String> response = paymentService.verifyPayment(postPaymentRequest, customUserDetail);

        return ApiUtils.success(HttpStatus.OK.value(), "결제사후검증에 성공하였습니다.", response.getBody());
    }

    @ApiOperation(value = "결제취소 API ", nickname = "승인된 결제를 취소하는 API")
    @PostMapping(path = "/cancel", headers = "X-API-VERSION=1")
    public Response<Object> cancelPayment(@RequestBody CancelPaymentRequest cancelPaymentRequest, @AuthenticationPrincipal CustomUserDetail customUserDetail) {

        String imp_uid = cancelPaymentRequest.getImp_uid();
        String merchant_uid = cancelPaymentRequest.getMerchant_uid();

       CancelPaymentResponse cancelPaymentResponse = paymentService.cancelPayment(imp_uid, merchant_uid, customUserDetail);

        return ApiUtils.success(HttpStatus.OK.value(), "결제가 취소되었습니다.", cancelPaymentResponse);
    }
}

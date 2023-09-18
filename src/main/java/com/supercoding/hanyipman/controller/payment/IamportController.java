package com.supercoding.hanyipman.controller.payment;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.payment.request.iamport.CancelPaymentRequest;
import com.supercoding.hanyipman.dto.payment.request.iamport.PostPaymentRequest;
import com.supercoding.hanyipman.dto.payment.request.iamport.RegisterPaymentRequest;
import com.supercoding.hanyipman.dto.payment.response.iamport.CancelPaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.iamport.GetOnePaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.iamport.PaymentPrepareResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.service.PaymentService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Api(tags = "결제 API (아임포트)")
public class IamportController {

    private final PaymentService paymentService;

    /** 엑세스 토큰 */
    @Operation(summary = "(아임포트) 엑세스 토큰 API ", description = "아임포트로부터 엑세스 토큰을 발급받는 API")
    @PostMapping(path = "/getToken", headers = "X-API-VERSION=1")
    public Response<Object> getToken() {
        String accessToken = paymentService.getToken();
        return ApiUtils.success(HttpStatus.OK.value(), "엑세스 토큰 발급에 성공했습니다. ", accessToken);
    }

    /** 결제사전검증 (엑세스 토큰 발급과정 들어있음) */
    @TimeTrace
    @Operation(summary = "(아임포트) 결제사전검증 API ", description = "아임포트에 결제번호와 결제예정금액을 사전에 등록하는 API")
    @PostMapping(path = "/prepare", headers = "X-API-VERSION=1")
    public Response<PaymentPrepareResponse> paymentPrepare(@RequestBody RegisterPaymentRequest registerPaymentRequest) {

        Long orderId = registerPaymentRequest.getOrderId();
        PaymentPrepareResponse paymentPrepareResponse = paymentService.paymentPrepare(JwtToken.user(), orderId);
        return ApiUtils.success(HttpStatus.OK.value(), "결제사전검증에 성공하였습니다.", paymentPrepareResponse);
    }
    @Operation(summary = "(아임포트) 결제내역 단건조회 API ", description = "아임포트에서 결제내역 단건을 조회 API")
    @GetMapping(path = "/{imp_uid}", headers = "X-API-VERSION=1")
    public Response<GetOnePaymentResponse> getOnePayment(@PathVariable("imp_uid") String imp_uid) {
        GetOnePaymentResponse getOnePaymentResponse = paymentService.paymentInfo(imp_uid);
        return ApiUtils.success(HttpStatus.OK.value(), "결제내역 단건조회에 성공하였습니다.", getOnePaymentResponse);
    }

    @TimeTrace
    @Operation(summary = "(아임포트) 결제사후검증 API ", description = "(아임포트)실제결제된 금액과 (주문)결제예상금액이 맞는지 확인하는 API")
    @PostMapping(path = "/complete", headers = "X-API-VERSION=1")
    public Response<Object> verifyPayment(@RequestBody PostPaymentRequest postPaymentRequest) {

        ResponseEntity<String> response = paymentService.verifyPayment(postPaymentRequest, JwtToken.user());

        return ApiUtils.success(HttpStatus.OK.value(), "결제사후검증에 성공하였습니다.", response.getBody());
    }
    @TimeTrace
    @Operation(summary = "(아임포트) 승인 후 결제취소 API ", description = "승인된 결제를 취소/환불하는 API")
    @PostMapping(path = "/iamport/cancel", headers = "X-API-VERSION=1")
    public Response<Object> cancelPayment(@RequestBody CancelPaymentRequest cancelPaymentRequest) {
        CancelPaymentResponse cancelPaymentResponse = paymentService.cancelPayment(cancelPaymentRequest, JwtToken.user());

        return ApiUtils.success(HttpStatus.OK.value(), "결제가 취소되었습니다.", cancelPaymentResponse);
    }
}

package com.supercoding.hanyipman.controller.payment;

import com.supercoding.hanyipman.dto.payment.request.iamport.AccessTokenRequest;
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
import io.swagger.annotations.ApiOperation;
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

    /* todo 엑세스 토큰, 버전 02 (입력폼 없음) */
    @ApiOperation(value = "(아임포트) 엑세스 토큰 API ", nickname = "아임포트로부터 엑세스 토큰을 발급받는 API")
    @PostMapping(path = "/getToken", headers = "X-API-VERSION=1")
    public Response<Object> getToken(@RequestBody AccessTokenRequest accessTokenRequest) {

        Long orderId = accessTokenRequest.getOrderId();
        String accessToken = paymentService.getToken(JwtToken.user(), orderId);

        return ApiUtils.success(HttpStatus.OK.value(), "엑세스 토큰 발급에 성공했습니다. ", accessToken);
    }


    /* todo 결제사전검증, 버전 02 (토큰 입력 없음) */
    @ApiOperation(value = "(아임포트) 결제사전검증 API ", nickname = "아임포트에 결제번호와 결제예정금액을 사전에 등록하는 API")
    @PostMapping(path = "/prepare", headers = "X-API-VERSION=1")
    public Response<PaymentPrepareResponse> paymentPrepare(@RequestBody RegisterPaymentRequest registerPaymentRequest) {

        Long orderId = registerPaymentRequest.getOrderId();
        PaymentPrepareResponse paymentPrepareResponse = paymentService.paymentPrepare(JwtToken.user(), orderId);

        return ApiUtils.success(HttpStatus.OK.value(), "결제사전검증에 성공하였습니다.", paymentPrepareResponse);
    }

    @ApiOperation(value = "(아임포트) 결제내역 단건조회 API ", nickname = "아임포트에서 결제내역 단건을 조회 API")
    @GetMapping(path = "/{imp_uid}/{orderId}", headers = "X-API-VERSION=1")
    public Response<GetOnePaymentResponse> getOnePayment(@PathVariable("imp_uid") String imp_uid, @PathVariable("orderId") Long orderId) {

        GetOnePaymentResponse getOnePaymentResponse = paymentService.paymentInfo(imp_uid, JwtToken.user(), orderId);
        return ApiUtils.success(HttpStatus.OK.value(), "결제내역 단건조회에 성공하였습니다.", getOnePaymentResponse);
    }

    @ApiOperation(value = "(아임포트) 결제사후검증 API ", nickname = "(아임포트)실제결제된 금액과 (주문)결제예상금액이 맞는지 확인하는 API")
    @PostMapping(path = "/complete", headers = "X-API-VERSION=1")
    public Response<Object> verifyPayment(@RequestBody PostPaymentRequest postPaymentRequest) {

        ResponseEntity<String> response = paymentService.verifyPayment(postPaymentRequest, JwtToken.user());

        return ApiUtils.success(HttpStatus.OK.value(), "결제사후검증에 성공하였습니다.", response.getBody());
    }

    @ApiOperation(value = "(아임포트) 결제취소 API ", nickname = "승인된 결제를 취소하는 API")
    @PostMapping(path = "/iamport/cancel", headers = "X-API-VERSION=1")
    public Response<Object> cancelPayment(@RequestBody CancelPaymentRequest cancelPaymentRequest) {
        CancelPaymentResponse cancelPaymentResponse = paymentService.cancelPayment(cancelPaymentRequest, JwtToken.user());

        return ApiUtils.success(HttpStatus.OK.value(), "결제가 취소되었습니다.", cancelPaymentResponse);
    }
}

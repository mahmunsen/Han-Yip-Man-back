package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.payment.request.PostPaymentRequest;
import com.supercoding.hanyipman.dto.payment.response.*;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.OrderTestRepository;
import org.springframework.beans.factory.annotation.Value;
import com.supercoding.hanyipman.dto.payment.request.PaymentPrepareRequest;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.PaymentErrorCode;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.PaymentRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderTestRepository orderTestRepository;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final RestTemplate restTemplate;
    private final SellerRepository sellerRepository;
    private static final String API_BASE_URL = "https://api.iamport.kr";

    @Value("${imp_key}")
    private String impKey;

    @Value("${imp_secret}")
    private String impSecret;

    /* todo 1. 토큰 발급 받기 (yml의 api_key, api_secret) */
    public String getToken(CustomUserDetail customUserDetail) {

        OrderTest orderTest = isOrderValid(customUserDetail);

        //HttpHeaders() 사용하려면 build.gradle에 configuration.processor추가해야.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        //MultiValueMap는 한 키에 여러 값이 올 수 있을 때 유용.
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("imp_key", impKey);
        params.add("imp_secret", impSecret);

        //new HttpEntity<>(Body, Headers): HttpEntity는 요청의 본문과 헤더를 설정하기 위해 사용된다. 즉, 요청내용 설정.
        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        //HttpEntity 상속받는 ResponseEntity는 응답을 처리하고 응답데이터를 추출하기 위해 사용된다. 즉, 응답내용.

        //restTemplate.exchange()메소드의 역할: 요청과 응답을 모두 처리, HTTP Post 요청으로 해당 엔드포인트로 httpEntity 객체를 전달하고 응답 타입(AccessTokenResponse.class)을 가져옴.
        ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(API_BASE_URL + "/users/getToken", HttpMethod.POST, httpEntity, AccessTokenResponse.class);

        // 응답이 200이면 성공 && 응답의 본문이 null이 아니면 응답 본문(헤더를 뺀 바디만)을 반환.
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody().getResponse().getAccess_token();
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
        }
    }

    /* todo 2. 결제사전검증 (토큰 입력 없음) */
    @Transactional
    public PaymentPrepareResponse paymentPrepare(CustomUserDetail customUserDetail) {

        // 엑세스 토큰 가져오는 메소드
        String access_token = getToken(customUserDetail);

        // 헤더에 엑세스 토큰 담는 메소드
        HttpHeaders headers = setHttpHeaders(access_token);

        // todo : merchant_uid (결제번호) 생성
        String merchant_uid = UUID.randomUUID().toString();

        OrderTest orderTest = isOrderValid(customUserDetail);

        PaymentPrepareRequest paymentPrepareRequest = PaymentPrepareRequest.toDto(orderTest);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("merchant_uid", merchant_uid);
        params.add("amount", paymentPrepareRequest.getAmount());

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<PaymentPrepareResponse> responseEntity = restTemplate.exchange(API_BASE_URL + "/payments/prepare", HttpMethod.POST, httpEntity, PaymentPrepareResponse.class);

        // 응답이 200이면 성공 && 응답의 본문이 null이 아니면 응답 본문(헤더를 뺀 바디만)을 반환.
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {

            // merchant_uid가 유효한지 확인
            if (!isMerchantUidValid(merchant_uid)) {
                throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
            }

            // amount가 0보다 큰지 확인
            if (paymentPrepareRequest.getAmount() <= 0) {
                throw new CustomException(PaymentErrorCode.IM_PORT_API_INVALID_AMOUNT);
            }

            return responseEntity.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
        }
    }

    /* todo 3. 결제내역 단건 조회 */
    public GetOnePaymentResponse paymentInfo(String imp_uid, CustomUserDetail customUserDetail) {

        //엑세스 토큰 메소드 불러옴
        String access_token = getToken(customUserDetail);

        HttpHeaders headers = setHttpHeaders(access_token);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("imp_uid", imp_uid);

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<GetOnePaymentResponse> responseEntity = restTemplate.exchange(API_BASE_URL + "/payments/" + imp_uid, HttpMethod.GET, httpEntity, GetOnePaymentResponse.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
        }
    }

    /* todo 4. 결제사후검증, 결제가 성공이어야만 주문접수가 된다 */
    @Transactional
    public ResponseEntity<String> verifyPayment(PostPaymentRequest postPaymentRequest, CustomUserDetail customUserDetail) {

        // Req 에서 추출한 merchant_uid, imp_uid
        String merchant_uid = postPaymentRequest.getMerchant_uid();
        String imp_uid = postPaymentRequest.getImp_uid();

        // 아임포트 결제 내역 단건 조회 API 에서 추출한 정보들
        Integer amount = paymentInfo(imp_uid, customUserDetail).getResponse().getAmount();
        String status = paymentInfo(imp_uid, customUserDetail).getResponse().getStatus();
        String payMethod = paymentInfo(imp_uid, customUserDetail).getResponse().getPay_method();
        String merchantUid = paymentInfo(imp_uid, customUserDetail).getResponse().getMerchant_uid();
        String impUid = paymentInfo(imp_uid, customUserDetail).getResponse().getImp_uid();

        // 입력값이 올바르지 않은 경우
        if (!impUid.equals(imp_uid)) {
            throw new CustomException(PaymentErrorCode.IM_PORT_MISMATCH_IMP_UID);
        } else if (!merchantUid.equals(merchant_uid)) {
            throw new CustomException(PaymentErrorCode.IM_PORT_MISMATCH_MERCHANT_UID);
        }

        // DB 에서 결제 되어야 할 금액 조회 (주문에서 찍힌 결제해야 할 금액 조회)
        OrderTest orderTest = isOrderValid(customUserDetail);
        Integer expectedPrice = orderTest.getTotalPrice();

        System.out.println("expectedPrice = " + expectedPrice);
        System.out.println("amount = " + amount);

        if (expectedPrice.equals(amount)) {
            // 금액 일치하면 결제 성공, 결제 성공한 건에 대해서 이 때 DB 저장
            // 응답으로 결제내역 내려줄지 여부는 프론트 UI 에 따라 달라질 예정.

            paymentRepository.save(new Payment(orderTest, merchant_uid, imp_uid, status, payMethod));

            return ResponseEntity.ok("Payment verified successfully");
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_PAYMENT_FAILED);
        }
    }

    // 주문내역 조회 API 만들기

    /* todo 5. 결제취소 (사용자의 요청에 의한 전체 취소)
     * 클라이언트에서 받은 주문번호(merchant_uid)를 사용해서 해당 주문의 결제정보를 Payments 테이블에서 조회
     * 아임포트 취소 API 를 호출하여 결제 취소를 요청
     * */

    public CancelPaymentResponse cancelPayment(String imp_uid, String merchant_uid, CustomUserDetail customUserDetail) {
        // 엑세스 토큰 메소드 불러옴
        String access_token = getToken(customUserDetail);

        HttpHeaders headers = setHttpHeaders(access_token);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("imp_uid", imp_uid);
        params.add("merchant_uid", merchant_uid);

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<CancelPaymentResponse> responseEntity = restTemplate.exchange(API_BASE_URL + "/payments/cancel", HttpMethod.POST, httpEntity, CancelPaymentResponse.class);

        // Payment: 결제내역 상태 변경, 취소날짜 삽입
        Payment paymentFound = paymentRepository.findPaymentByImpUidAndMerchantUid(imp_uid, merchant_uid);
        paymentFound.setCancellationDate(Instant.now());
        paymentFound.setPaymentStatus("cancelled");
        paymentRepository.save(paymentFound);

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
        }
    }

    // 로그인한 소비자 아이디와 결제주문건의 소비자 아이디와 일치하는 경우만
    private OrderTest isOrderValid(CustomUserDetail customUserDetail) {
        User validUser = userRepository.findById(customUserDetail.getUserId()).orElseThrow(() -> new CustomException(PaymentErrorCode.IM_PORT_NON_EXISTENT_MEMBER));

        Boolean areYouBuyer = buyerRepository.existsByUser(validUser);

        if (Boolean.TRUE.equals(areYouBuyer)) {
            Buyer buyer = buyerRepository.findByUser(validUser);

            // OrderTest를 orderId와 BuyerId로 찾아야 -> orderId로만 찾아도 될지도?
            OrderTest orderTest = orderTestRepository.findOrderTestById(3L).orElseThrow(() -> new CustomException(PaymentErrorCode.IM_PORT_NO_ORDER));
            buyer.getId();

            if (orderTest.getBuyerId() == buyer.getId()) {
                return orderTest;

            } else throw new CustomException(PaymentErrorCode.IM_PORT_MISMATCH_ORDER_AND_BUYER);
        } else throw new CustomException(PaymentErrorCode.IM_PORT_NOT_BUYER);
    }

    // HttpHeaders 셋팅
    private static HttpHeaders setHttpHeaders(String access_token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + access_token);
        return headers;
    }

    // merchant_uid 유효성 검사
    public Boolean isMerchantUidValid(String merchant_uid) {
        return Pattern.matches("^[a-zA-Z0-9-]+$", merchant_uid);
    }

}

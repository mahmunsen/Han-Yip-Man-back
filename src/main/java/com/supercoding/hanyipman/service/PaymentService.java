package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.payment.request.iamport.CancelPaymentRequest;
import com.supercoding.hanyipman.dto.payment.request.iamport.PostPaymentRequest;
import com.supercoding.hanyipman.dto.payment.request.kakaopay.KakaoPayCancelRequest;
import com.supercoding.hanyipman.dto.payment.response.iamport.AccessTokenResponse;
import com.supercoding.hanyipman.dto.payment.response.iamport.CancelPaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.iamport.GetOnePaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.iamport.PaymentPrepareResponse;
import com.supercoding.hanyipman.dto.payment.response.kakaopay.*;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.repository.OrderTestRepository;
import org.springframework.beans.factory.annotation.Value;
import com.supercoding.hanyipman.dto.payment.request.iamport.PaymentPrepareRequest;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.PaymentErrorCode;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.PaymentRepository;
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
    private final BuyerRepository buyerRepository;
    private final RestTemplate restTemplate;
    private static final String API_BASE_URL = "https://api.iamport.kr";
    private static final String KAKAOPAY_BASE_URL = "https://kapi.kakao.com";

    @Value("${imp_key}")
    private String impKey;

    @Value("${imp_secret}")
    private String impSecret;

    @Value("${kakaopay_key}")
    private String kakaoKey;

    /* todo 1. (아임포트) 토큰 발급 받기 (yml의 api_key, api_secret) */

    public String getToken(User user, Long orderId) {

        // todo 주문건 상태가 이미 PAID or CANCELLED 일 때 토큰 발급 요청하면 엑세스 토큰 발급부터 막아야 하나?
        OrderTest orderTest = isOrderValid(user, orderId);

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

    /* todo 2.  (아임포트) 결제사전검증 (토큰 입력 없음) */

    @Transactional
    public PaymentPrepareResponse paymentPrepare(User user, Long orderId) {

        // 엑세스 토큰 가져오는 메소드
        String access_token = getToken(user, orderId);

        // 헤더에 엑세스 토큰 담는 메소드
        HttpHeaders headers = setHttpHeaders(access_token);

        // todo merchant_uid (결제번호) 생성
        String merchant_uid = UUID.randomUUID().toString();

        // 해당 주문건 찾기
        OrderTest orderTest = isOrderValid(user, orderId);

        // 해당 주문건의 상태값에 따른 처리
        checkOrderStatus(orderTest);

        // 해당 주문건, DTO로 변환
        PaymentPrepareRequest paymentPrepareRequest = PaymentPrepareRequest.toDto(orderTest);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("merchant_uid", merchant_uid);
        params.add("amount", paymentPrepareRequest.getAmount());

        // 아임포트에 보낼 두 파라미터 넣어서 httpEntity 생성
        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        // 아임포트로 요청 보내고 응답 으로 responseEntity 반환받음
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

    /* todo 3. (아임포트) 결제내역 단건 조회 */

    public GetOnePaymentResponse paymentInfo(String imp_uid, User user, Long orderId) {

        // 엑세스 토큰 메소드 불러옴
        String access_token = getToken(user, orderId);

        // 헤더 설정
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

    /* todo 4. (아임포트) 결제사후검증, 결제가 성공이어야만 주문접수가 된다 */
    @Transactional
    public ResponseEntity<String> verifyPayment(PostPaymentRequest postPaymentRequest, User user) {

        // Req 에서 추출한 merchant_uid, imp_uid
        String merchant_uid = postPaymentRequest.getMerchant_uid();
        String imp_uid = postPaymentRequest.getImp_uid();
        Long orderId = postPaymentRequest.getOrderId();

        // 아임포트 결제 내역 단건 조회 API 에서 추출한 정보들
        Integer amount = paymentInfo(imp_uid, user, orderId).getResponse().getAmount();
        String status = paymentInfo(imp_uid, user, orderId).getResponse().getStatus();
        String payMethod = paymentInfo(imp_uid, user, orderId).getResponse().getPg_provider();
        String merchantUid = paymentInfo(imp_uid, user, orderId).getResponse().getMerchant_uid();
        String impUid = paymentInfo(imp_uid, user, orderId).getResponse().getImp_uid();

        // 입력값이 올바르지 않은 경우
        if (!impUid.equals(imp_uid)) {
            throw new CustomException(PaymentErrorCode.IM_PORT_MISMATCH_IMP_UID);
        } else if (!merchantUid.equals(merchant_uid)) {
            throw new CustomException(PaymentErrorCode.IM_PORT_MISMATCH_MERCHANT_UID);
        }
        // 해당 주문건 찾기
        OrderTest orderTest = isOrderValid(user, orderId);

        // 해당 주문건의 상태값에 따른 처리
        checkOrderStatus(orderTest);

        // 해당 주문건의 가게의 업주 찾기
        Seller seller = orderTestRepository.findOrderTestBySeller(orderTest.getShopId());

        // DB 에서 결제 되어야 할 금액 조회 (주문에서 찍힌 total_price, 금액 조회)
        Integer expectedPrice = orderTest.getTotalPrice();

        if (expectedPrice.equals(amount)) {
            // 금액 일치하면 결제 성공, 결제 성공한 건에 대해서 이 때 DB 저장
            paymentRepository.save(new Payment(orderTest, merchant_uid, imp_uid, status, payMethod, seller.getId()));
            orderTest.setStatus("PAID"); // 주문 상태값, 결제됨으로 변경
            orderTestRepository.save(orderTest); // 주문 엔티티 업데이트(주문 상태 변경)

            return ResponseEntity.ok("Payment verified successfully");
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_PAYMENT_FAILED);
        }
    }

    /* todo 5. (아임포트) 결제취소 (사용자의 요청에 의한 전체 취소)
     * 클라이언트에서 받은 주문번호(merchant_uid)를 사용해서 해당 주문의 결제정보를 Payments 테이블에서 조회
     * 아임포트 취소 API 를 호출하여 결제 취소를 요청
     * */
    @Transactional
    public CancelPaymentResponse cancelPayment(CancelPaymentRequest cancelPaymentRequest, User user) {
        String imp_uid = cancelPaymentRequest.getImp_uid();
        String merchant_uid = cancelPaymentRequest.getMerchant_uid();
        Long orderId = cancelPaymentRequest.getOrderId();

        // 엑세스 토큰 메소드 불러옴
        String access_token = getToken(user, orderId);

        // 헤더 설정
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

        // 주문의 isDeleted = true 로 변경
        orderTestRepository.save(orderTestRepository.findById(orderId).map(orderTest -> {
//          orderTest.setIsDeleted(true);
            orderTest.setStatus("CANCELLED");
            return orderTest;
        }).orElseThrow());

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
        }
    }



    /* todo----------------------------------------------------------------------------------*/
    /* todo----------------------------------------------------------------------------------*/
    /* todo----------------------------------------------------------------------------------*/



    /* todo 1. (카카오페이) 결제준비 */
    @Transactional
    public KakaoPayReadyResponse kakaopayReady(Long orderId, User user) {

        OrderTest orderTest = isOrderValid(user, orderId);
        // 해당 주문건의 상태값에 따른 처리
        checkOrderStatus(orderTest);

        // todo merchant_uid (결제번호) 생성
        String merchant_uid = UUID.randomUUID().toString();

        RestTemplate kakaoTemplate = new RestTemplate();

        // 카카오서버로 요청할 헤더
        HttpHeaders headers = setKakaoHeader();

        // 카카오서버로 요청할 바디
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", merchant_uid);
        params.add("partner_user_id", orderId);
        params.add("item_name", "맛나피자"); // todo 추후 수정, 예시
        params.add("quantity", 1);
        params.add("total_amount", orderTest.getTotalPrice());
        params.add("tax_free_amount", 0);
        params.add("approval_url", "http://localhost:8080/api/payments/approve/" + orderId); // todo url들 추후 수정 가능
        params.add("cancel_url", "http://localhost:8080/api/payments/kakaoPayCancel/" + orderId);
        params.add("fail_url", "http://localhost:8080/api/payments/kakaoPayFail/" + orderId);

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        KakaoPayReadyResponse kakaoPayReadyResponse = kakaoTemplate.postForObject(KAKAOPAY_BASE_URL + "/v1/payment/ready", httpEntity, KakaoPayReadyResponse.class);

        if (kakaoPayReadyResponse != null) {
            // 응답값으로 merchant_uid 포함
            kakaoPayReadyResponse.setMerchant_uid(merchant_uid);
            // 해당 주문건 매장의 업주
            Seller seller = orderTestRepository.findOrderTestBySeller(orderTest.getShopId());
            // 결제사전준비에서 payment 새로 생성
            paymentRepository.save(Payment.from(orderTest, merchant_uid, kakaoPayReadyResponse.getTid(), seller.getId()));

            return kakaoPayReadyResponse;
        } else {
            throw new CustomException(PaymentErrorCode.KAKAOPAY_API_COMMUNICATION_ERROR);
        }
    }


    /* todo 2. (카카오페이) 결제승인요청 */
    @Transactional
    public KakaoPayApproveResponse kakaopayApprove(String pgToken, User user, Long orderId) {

        // 필요 파라미터들
        OrderTest orderTest = isOrderValid(user, orderId);
        Payment payment = paymentRepository.findPaymentByOrderTestId(orderTest).orElseThrow(()-> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NOT_PAID_YET));
        String tid = payment.getImpUid();
        String merchant_uid = payment.getMerchantUid();

        // 해당 주문건의 상태값에 따른 처리
        checkOrderStatus(orderTest);

        RestTemplate kakaoTemplate = new RestTemplate();

        // 카카오서버로 요청할 헤더
        HttpHeaders headers = setKakaoHeader();

        // 카카오서버로 요청할 바디
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("partner_order_id", merchant_uid);
        params.add("partner_user_id", orderId);
        params.add("pg_token", pgToken);
        params.add("total_amount", orderTest.getTotalPrice()); // 필수는 아님

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        KakaoPayApproveResponse kakaoPayApproveResponse = kakaoTemplate.postForObject(KAKAOPAY_BASE_URL + "/v1/payment/approve", httpEntity, KakaoPayApproveResponse.class);

        if (kakaoPayApproveResponse != null) {
            // 결제내역 디비에 저장
            orderTest.setStatus("PAID");
            payment.setPaymentStatus("paid");
            paymentRepository.save(payment);
            orderTestRepository.save(orderTest);
            return kakaoPayApproveResponse;
        } else {
            throw new CustomException(PaymentErrorCode.KAKAOPAY_API_COMMUNICATION_ERROR);
        }
    }

    /* todo 3. (카카오페이) 결제내역 단건조회 */
    public KakaoPayViewPayResponse kakaopayViewOnePayment(String tid, Long orderId, User user) {
        // 해당 주문건
        OrderTest orderTest = isOrderValid(user, orderId);

        RestTemplate kakaoTemplate = new RestTemplate();

        // 카카오서버로 요청할 헤더
        HttpHeaders headers = setKakaoHeader();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        String cid = "TC0ONETIME";
        params.add("cid", cid);
        params.add("tid", tid);

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        KakaoPayViewPayResponse kakaoPayViewPayResponse = kakaoTemplate.exchange(KAKAOPAY_BASE_URL + "/v1/payment/order?cid=" + cid + "&tid=" + tid, HttpMethod.GET, httpEntity, KakaoPayViewPayResponse.class).getBody();

        if (kakaoPayViewPayResponse != null) {
            return kakaoPayViewPayResponse;
        } else {
            throw new CustomException(PaymentErrorCode.KAKAOPAY_API_COMMUNICATION_ERROR);
        }
    }

    /* todo 4. (카카오페이) 결제건 취소 */
    public KakaoPayCancelResponse afterKakaoPayCancel(KakaoPayCancelRequest kakaoPayCancelRequest, User user) {
        // 주문번호
        Long orderId = kakaoPayCancelRequest.getOrderId();
        // 해당 주문건과 결제건
        OrderTest orderTest = isOrderValid(user, orderId);
        Payment payment = paymentRepository.findPaymentByOrderTestId(orderTest).orElseThrow(()-> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NOT_PAID_YET));
        // 해당 주문건의 상태값에 따른 처리
        if (orderTest.getStatus().equals("CANCELLED")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_CANCELLED);
        }
        RestTemplate kakaoTemplate = new RestTemplate();
        // 카카오서버로 요청할 헤더
        HttpHeaders headers = setKakaoHeader();

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", payment.getImpUid());
//        params.add("partner_user_id", orderId);
        params.add("cancel_amount", payment.getTotalAmount());
        params.add("cancel_tax_free_amount", 0);

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        KakaoPayCancelResponse kakaoPayCancelResponse = kakaoTemplate.exchange(KAKAOPAY_BASE_URL + "/v1/payment/cancel", HttpMethod.POST, httpEntity, KakaoPayCancelResponse.class).getBody();

        if (kakaoPayCancelResponse != null) {
            // Payment: 결제내역 상태 변경, 취소날짜 삽입
            payment.setCancellationDate(Instant.now());
            payment.setPaymentStatus("cancelled");
            paymentRepository.save(payment);

            // 주문 상태: 취소로 변경
            orderTest.setStatus("CANCELLED");
            // orderTest.setIsDeleted(true);
            orderTestRepository.save(orderTest);

            return kakaoPayCancelResponse;
        } else {
            throw new CustomException(PaymentErrorCode.KAKAOPAY_API_COMMUNICATION_ERROR);
        }
    }

    /* todo 5. (카카오페이) 결제중 취소 및 실패 */
    public void kakaoPayCancelOrFail(Long orderId) {
        Payment payment = paymentRepository.findPaymentByOrderTestIdId(orderId);
        if (payment.getPaymentStatus().equals("ready")){  // todo "ready" 인 경우에만..? 추후 수정 가능.
            paymentRepository.deleteById(payment.getId());
        }
    }



    /* todo----------------------------------------------------------------------------------*/
    /* todo----------------------------------------------------------------------------------*/
    /* todo----------------------------------------------------------------------------------*/



    /* todo 외부 메소드: isOrderValid */
    // 로그인한 소비자 아이디와 결제주문건의 소비자 아이디와 일치하는 경우만
    private OrderTest isOrderValid(User user, Long orderId) {

        Boolean areYouBuyer = buyerRepository.existsByUser(user);

        if (Boolean.TRUE.equals(areYouBuyer)) {
            Buyer buyer = buyerRepository.findByUser(user);

            // OrderTest를 orderId로 찾기
            OrderTest orderTest = orderTestRepository.findOrderTestById(orderId).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NO_ORDER));

            if (orderTest.getBuyerId().getId() == buyer.getId()) {
                return orderTest;

            } else throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_MISMATCH_ORDER_AND_BUYER);
        } else throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_NOT_BUYER);
    }


    /* todo 외부 메소드: 주문의 상태값 체크 */
    public void checkOrderStatus(OrderTest orderTest) throws CustomException {
        if (orderTest.getStatus().equals("PAID")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_PAID);
        } else if (orderTest.getStatus().equals("CANCELLED")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_CANCELLED);
        }
    }

    /* todo 외부 메소드: (아임포트) HttpHeaders 셋팅 */
    private static HttpHeaders setHttpHeaders(String access_token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + access_token);
        return headers;
    }


    /* todo 외부 메소드: (카카오서버) HttpHeaders 셋팅 */
    private HttpHeaders setKakaoHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", " KakaoAK " + kakaoKey);
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        return headers;
    }


    /* todo 외부 메소드: merchant_uid 유효성 검사 */
    public Boolean isMerchantUidValid(String merchant_uid) {
        return Pattern.matches("^[a-zA-Z0-9-]+$", merchant_uid);
    }
}


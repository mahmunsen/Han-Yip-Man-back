package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.order.response.OrderNoticeSellerResponse;
import com.supercoding.hanyipman.dto.payment.request.iamport.CancelPaymentRequest;
import com.supercoding.hanyipman.dto.payment.request.iamport.PostPaymentRequest;
import com.supercoding.hanyipman.dto.payment.request.kakaopay.KakaoPayCancelRequest;
import com.supercoding.hanyipman.dto.payment.response.iamport.AccessTokenResponse;
import com.supercoding.hanyipman.dto.payment.response.iamport.CancelPaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.iamport.GetOnePaymentResponse;
import com.supercoding.hanyipman.dto.payment.response.iamport.PaymentPrepareResponse;
import com.supercoding.hanyipman.dto.payment.response.kakaopay.*;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.enums.EventName;
import com.supercoding.hanyipman.enums.OrderStatus;
import com.supercoding.hanyipman.error.domain.CartErrorCode;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.repository.cart.CartRepository;
import com.supercoding.hanyipman.repository.cart.EmCartRepository;
import com.supercoding.hanyipman.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import com.supercoding.hanyipman.dto.payment.request.iamport.PaymentPrepareRequest;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.PaymentErrorCode;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final BuyerRepository buyerRepository;
    private final EmCartRepository emCartRepository;
    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;
    private final OrderService orderService;
    private final SseEventService sseService;
    private static final String API_BASE_URL = "https://api.iamport.kr";
    private static final String KAKAOPAY_BASE_URL = "https://kapi.kakao.com";

    @Value("${imp_key}")
    private String impKey;

    @Value("${imp_secret}")
    private String impSecret;

    @Value("${kakaopay_key}")
    private String kakaoKey;

    /**
     * 1. (아임포트) 토큰 발급 받기 (yml의 api_key, api_secret)
     */

    public String getToken() {
        // 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // MultiValueMap
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("imp_key", impKey);
        params.add("imp_secret", impSecret);

        // HttpEntity는 요청의 본문과 헤더를 설정하기 위해 사용
        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        // ResponseEntity는 응답을 처리하고 응답데이터를 추출하기 위해 사용
        ResponseEntity<AccessTokenResponse> responseEntity = restTemplate.exchange(API_BASE_URL + "/users/getToken", HttpMethod.POST, httpEntity, AccessTokenResponse.class);

        // 응답이 200이면 성공 && 응답의 본문이 null이 아니면 응답 본문(헤더를 뺀 바디만)을 반환.
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody().getResponse().getAccess_token();
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
        }
    }

    /**
     * 2.  (아임포트) 결제사전검증 (토큰 입력 없음)
     */
    @Transactional
    public PaymentPrepareResponse paymentPrepare(User user, Long orderId) {
        // 엑세스 토큰
        String access_token = getToken();
        // 헤더
        HttpHeaders headers = setHttpHeaders(access_token);
        // merchant_uid (결제번호)
        String merchant_uid = UUID.randomUUID().toString();
        // 해당 주문건
        Order order = isOrderValid(user, orderId);
        // 해당 주문건의 상태값에 따른 처리
        checkOrderStatus(order);
        // 해당 주문건, DTO로 변환
        PaymentPrepareRequest paymentPrepareRequest = PaymentPrepareRequest.toDto(order);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("merchant_uid", merchant_uid);
        params.add("amount", paymentPrepareRequest.getAmount());

        // 아임포트에 보낼 두 파라미터 넣어서 httpEntity 생성
        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
        // 아임포트로 요청 보내고 응답 으로 responseEntity 반환받음
        ResponseEntity<PaymentPrepareResponse> paymentPrepareResponse = restTemplate.exchange(API_BASE_URL + "/payments/prepare", HttpMethod.POST, httpEntity, PaymentPrepareResponse.class);

        if (paymentPrepareResponse.getStatusCode() == HttpStatus.OK && paymentPrepareResponse.getBody() != null) {
            // 해당 주문건의 가게의 업주 찾기
            Seller seller = getSeller(order);
            // 해당 결제건 여부
            isPaymentExistent(order);
            // todo 결제사전준비에서 payment 최초 생성(imp_uid 일단 제외하고, imp_uid는 프론트에서 받아옴)
            paymentRepository.save(Payment.importFrom(order, merchant_uid, seller.getId()));
            return paymentPrepareResponse.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
        }
    }

    /**
     * 3. (아임포트) 결제내역 단건 조회
     */

    public GetOnePaymentResponse paymentInfo(String imp_uid) {
        // 엑세스 토큰
        String access_token = getToken();
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

    /**
     * 4. (아임포트) 결제사후검증, 결제가 성공이어야만 주문접수가 된다
     */
    @Transactional
    public ResponseEntity<String> verifyPayment(PostPaymentRequest postPaymentRequest, User user) {
        // Req 에서 추출한 merchant_uid, imp_uid  -> merchant_uid 디비에서 불러오는 것으로 변경됨
        // String merchant_uid = postPaymentRequest.getMerchant_uid();
        String imp_uid = postPaymentRequest.getImp_uid();
        Long orderId = postPaymentRequest.getOrderId();
        // 해당 주문건 찾기
        Order order = isOrderValid(user, orderId);

        // 해당 결제건 찾기 (이 때는 결제건이 존재해야 한다)
        Payment payment = getPayment(orderId);
        String merchant_uid = payment.getMerchantUid();

        // 해당 주문건의 상태값에 따른 처리
        checkOrderStatus(order);
        // 해당 결제건의 상태값에 따른 처리: 상태값이 "ready" 여야한다. "ready"가 아닌 경우 에러
        checkPaymentStatus(payment);

        // 아임포트 결제 내역 단건 조회 API 에서 추출한 정보들
        Integer amount = paymentInfo(imp_uid).getResponse().getAmount();
        String status = paymentInfo(imp_uid).getResponse().getStatus();
        String merchantUid = paymentInfo(imp_uid).getResponse().getMerchant_uid();
        String impUid = paymentInfo(imp_uid).getResponse().getImp_uid();

        // 입력값이 올바르지 않은 경우
        if (!impUid.equals(imp_uid)) {
            throw new CustomException(PaymentErrorCode.IM_PORT_MISMATCH_IMP_UID);
        } else if (!merchantUid.equals(merchant_uid)) {
            throw new CustomException(PaymentErrorCode.IM_PORT_MISMATCH_MERCHANT_UID);
        }
        if (status.equals("failed")) {
            paymentRepository.deleteById(payment.getId());
            paymentFailed(orderId, order);
            return ResponseEntity.badRequest().body("결제가 실패하였습니다.");
        }
        // DB 에서 결제 되어야 할 금액 조회
        Integer expectedPrice = order.getTotalPrice();

        if (expectedPrice.equals(amount)) {
            payment.setImpUid(impUid); // 결제에 imp_uid 저장
            payment.setPaymentStatus(status); // 결제 상태값, ready -> paid 로 저장
            order.setOrderStatus(OrderStatus.valueOf("PAID")); // 주문 상태값, WAIT -> PAID로 변경
            setOrderSequence(order);
            orderRepository.save(order); // 주문 엔티티 업데이트(주문 상태 변경)
            //주문 알림 기능
            OrderNoticeSellerResponse orderNoticeResponse = orderService.findOrderNoticeToSeller(user.getId(), order.getId());
            sseService.validSendMessage(user.getId(), EventName.NOTICE_ORDER, orderNoticeResponse);

            return ResponseEntity.ok("결제가 성공했습니다.");
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_PAYMENT_FAILED);
        }
    }

    /**
     * 5. (아임포트) 결제취소 (사용자의 요청에 의한 전체 취소)
     * 클라이언트에서 받은 주문번호(merchant_uid)를 사용해서 해당 주문의 결제정보를 Payments 테이블에서 조회
     * 아임포트 취소 API 를 호출하여 결제 취소를 요청
     */
    @CacheEvict(value = "viewOrderDetail", allEntries = true)
    @Transactional
    public CancelPaymentResponse cancelPayment(CancelPaymentRequest cancelPaymentRequest, User user) {
        // imp_uid, merchant_uid: cancelPaymentReq -> payment db 에서 가져오기
        Long orderId = cancelPaymentRequest.getOrderId();

        // 엑세스 토큰 메소드 불러옴
        String access_token = getToken();
        // 해당 주문건 찾기
        Order order = isOrderValid(user, orderId);
        // 해당 결제건 찾기
        Payment payment = getPayment(orderId);
        // 해당 주문건의 상태값에 따른 처리: "PAID" 가 아닌 경우 불가
        checkOrderStatusAfterPay(order);
        // 해당 결제건의 상태값에 따른 처리: "paid" 가 아닌 경우 불가
        checkPaymentStatusAfterPay(payment);

        String imp_uid = payment.getImpUid();
        String merchant_uid = payment.getMerchantUid();

        // 헤더 설정
        HttpHeaders headers = setHttpHeaders(access_token);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("imp_uid", imp_uid);
        params.add("merchant_uid", merchant_uid);

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<CancelPaymentResponse> responseEntity = restTemplate.exchange(API_BASE_URL + "/payments/cancel", HttpMethod.POST, httpEntity, CancelPaymentResponse.class);

        // Payment: 상태값 변경, 취소날짜 삽입
        payment.setCancellationDate(Instant.now());
        payment.setPaymentStatus("canceled");
        paymentRepository.save(payment);
        // Order: 상태값 변경
        order.setOrderStatus(OrderStatus.valueOf("CANCELED"));
        orderRepository.save(order);

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.IM_PORT_API_COMMUNICATION_ERROR);
        }
    }


    /* todo----------------------------------------------------------------------------------*/
    /* todo----------------------------------------------------------------------------------*/
    /* todo----------------------------------------------------------------------------------*/



    /**
     * 1. (카카오페이) 결제준비
     */
    @Transactional
    public KakaoPayReadyResponse kakaopayReady(Long orderId, User user) {
        Buyer buyer = getBuyer(user);

        Order order = getOrder(orderId);

        isBuyerAndOrderBuyerSame(buyer, order);
        // 해당 주문건의 카트들 가져오기(소비자 아이디와 주문 아이디로)
        List<Cart> carts = getCarts(buyer.getId(), orderId);
        // 메뉴이름들
        List<String> menuNames = carts.stream().map(cart -> cart.getMenu().getName()).collect(Collectors.toList());
        // 주문명
        String orderName = getOrderName(carts, menuNames);
        // 해당 주문건의 상태값에 따른 처리
        checkOrderStatus(order);
        // merchant_uid (결제번호)
        String merchant_uid = UUID.randomUUID().toString();

        RestTemplate kakaoTemplate = new RestTemplate();
        // 카카오서버로 요청할 헤더
        HttpHeaders headers = setKakaoHeader();

        // 카카오서버로 요청할 바디
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", merchant_uid);
        params.add("partner_user_id", orderId);
        params.add("item_name", orderName);
        params.add("quantity", 1);
        params.add("total_amount", order.getTotalPrice());
        params.add("tax_free_amount", 0);
        params.add("approval_url", "http://localhost:8080/api/payments/approve/" + orderId); // todo url들 추후 수정 가능
        params.add("cancel_url", "http://localhost:8080/api/payments/kakaoPayCancel/" + orderId);
        params.add("fail_url", "http://localhost:8080/api/payments/kakaoPayFail/" + orderId);

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoPayReadyResponse> kakaoPayReadyResponse = kakaoTemplate.exchange(KAKAOPAY_BASE_URL + "/v1/payment/ready", HttpMethod.POST, httpEntity, KakaoPayReadyResponse.class);

        if (kakaoPayReadyResponse.getStatusCode() == HttpStatus.OK && kakaoPayReadyResponse.getBody() != null) {
            // 응답값으로 merchant_uid 포함
            kakaoPayReadyResponse.getBody().setMerchant_uid(merchant_uid);
            // 해당 주문건 매장의 업주
            Seller seller = getSeller(order);
            // 해당 결제건 여부
            isPaymentExistent(order);
            // 결제사전준비에서 payment 새로 생성 (tid 저장)
            paymentRepository.save(Payment.kakaoFrom(order, merchant_uid, kakaoPayReadyResponse.getBody().getTid(), seller.getId()));
            return kakaoPayReadyResponse.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.KAKAOPAY_API_COMMUNICATION_ERROR);
        }
    }


    /**
     * 2. (카카오페이) 결제승인요청
     */
    @Transactional
    public KakaoPayApproveResponse kakaopayApprove(String pgToken, User user, Long orderId) {
        // 필요 파라미터들
        Order order = isOrderValid(user, orderId);
        Payment payment = getPayment(orderId);
        String tid = payment.getImpUid();
        String merchant_uid = payment.getMerchantUid();

        // 해당 주문건의 상태값에 따른 처리
        checkOrderStatus(order);
        // 해당 결제건의 상태값에 따른 처리: 상태값이 "ready" 여야한다. "ready"가 아닌 경우 에러
        checkPaymentStatus(payment);

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
        params.add("total_amount", order.getTotalPrice()); // 필수는 아님

        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoPayApproveResponse> kakaoPayApproveResponse = kakaoTemplate.exchange(KAKAOPAY_BASE_URL + "/v1/payment/approve", HttpMethod.POST, httpEntity, KakaoPayApproveResponse.class);

        if (kakaoPayApproveResponse.getStatusCode() == HttpStatus.OK && kakaoPayApproveResponse != null) {
            // 결제내역 디비에 저장
            order.setOrderStatus(OrderStatus.valueOf("PAID"));
            setOrderSequence(order);
            payment.setPaymentStatus("paid");
            paymentRepository.save(payment);
            orderRepository.save(order);

            OrderNoticeSellerResponse sseOrderResponse = orderService.findOrderNoticeToSeller(user.getId(), order.getId());
            sseService.validSendMessage(user.getId(), EventName.NOTICE_ORDER, sseOrderResponse);

            return kakaoPayApproveResponse.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.KAKAOPAY_API_COMMUNICATION_ERROR);
        }
    }

//    /**
//     * 2. (카카오페이) 결제승인요청
//     */
//    @Transactional
//    public ResponseEntity<String> kakaopayApprove(String pgToken, User user, Long orderId) {
//        // 필요 파라미터들
//        Order order = isOrderValid(user, orderId);
//        Payment payment = getPayment(orderId);
//        String tid = payment.getImpUid();
//        String merchant_uid = payment.getMerchantUid();
//
//        // 해당 주문건의 상태값에 따른 처리
//        checkOrderStatus(order);
//        // 해당 결제건의 상태값에 따른 처리: 상태값이 "ready" 여야한다. "ready"가 아닌 경우 에러
//        checkPaymentStatus(payment);
//
//        RestTemplate kakaoTemplate = new RestTemplate();
//        // 카카오서버로 요청할 헤더
//        HttpHeaders headers = setKakaoHeader();
//        // 카카오서버로 요청할 바디
//        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
//        params.add("cid", "TC0ONETIME");
//        params.add("tid", tid);
//        params.add("partner_order_id", merchant_uid);
//        params.add("partner_user_id", orderId);
//        params.add("pg_token", pgToken);
//        params.add("total_amount", order.getTotalPrice()); // 필수는 아님
//
//        HttpEntity<?> httpEntity = new HttpEntity<>(params, headers);
//
//        ResponseEntity<KakaoPayApproveResponse> kakaoPayApproveResponse = kakaoTemplate.exchange(KAKAOPAY_BASE_URL + "/v1/payment/approve", HttpMethod.POST, httpEntity, KakaoPayApproveResponse.class);
//
//        if (kakaoPayApproveResponse.getStatusCode() == HttpStatus.OK && kakaoPayApproveResponse != null) {
//            // 결제내역 디비에 저장
//            order.setOrderStatus(OrderStatus.valueOf("PAID"));
//            payment.setPaymentStatus("paid");
//            paymentRepository.save(payment);
//            orderRepository.save(order);
//
////            OrderNoticeResponse sseOrderResponse = orderService.findOrder(user.getId(), order.getId());
////            sseService.validSendMessage(user.getId(), EventName.NOTICE_ORDER, sseOrderResponse);
//
//            return ResponseEntity.ok("결제가 성공했습니다.");
//        } else {
//            throw new CustomException(PaymentErrorCode.KAKAOPAY_API_COMMUNICATION_ERROR);
//        }
//    }


    /**
     * 3. (카카오페이) 결제내역 단건조회
     */
    public KakaoPayViewPayResponse kakaopayViewOnePayment(String tid) {

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

    /**
     * 4. (카카오페이) 결제건 취소
     */
    @CacheEvict(value = "viewOrderDetail", allEntries = true)
    @Transactional
    public KakaoPayCancelResponse afterKakaoPayCancel(KakaoPayCancelRequest kakaoPayCancelRequest, User user) {
        // 주문번호
        Long orderId = kakaoPayCancelRequest.getOrderId();
        // 해당 주문건과 결제건
        Order order = isOrderValid(user, orderId);
        Payment payment = getPayment(orderId);
        // 해당 주문건의 상태값에 따른 처리: "PAID" 가 아닌 경우 불가
        checkOrderStatusAfterPay(order);
        // 해당 결제건의 상태값에 따른 처리: "paid" 가 아닌 경우 불가
        checkPaymentStatusAfterPay(payment);

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

        ResponseEntity<KakaoPayCancelResponse> kakaoPayCancelResponse = kakaoTemplate.exchange(KAKAOPAY_BASE_URL + "/v1/payment/cancel", HttpMethod.POST, httpEntity, KakaoPayCancelResponse.class);

        if (kakaoPayCancelResponse.getStatusCode() == HttpStatus.OK && kakaoPayCancelResponse != null) {
            // Payment: 결제내역 상태 변경, 취소날짜 삽입
            payment.setCancellationDate(Instant.now());
            payment.setPaymentStatus("canceled");
            paymentRepository.save(payment);

            // 주문 상태: 취소로 변경
            order.setOrderStatus(OrderStatus.valueOf("CANCELED"));
            orderRepository.save(order);

            return kakaoPayCancelResponse.getBody();
        } else {
            throw new CustomException(PaymentErrorCode.KAKAOPAY_API_COMMUNICATION_ERROR);
        }
    }


    /**
     * 5. (카카오페이) 결제중 취소 및 실패
     */
    //  결제중 취소할 때는 order, isDeleted = true 해주고 CANCELED, 장바구니 isDeleted = false로 바꿔주기
    @Transactional
    public void kakaoPayCancelOrFail(Long orderId) {
        Payment payment = getPayment(orderId);
        if (payment.getPaymentStatus().equals("ready")) {
            // 결제건 삭제
            paymentRepository.deleteById(payment.getId());
        }
        Order order = orderRepository.findOrderById(orderId).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NO_ORDER));
        paymentFailed(orderId, order);
    }



    /* todo----------------------------------------------------------------------------------*/
    /* todo----------------------------------------------------------------------------------*/
    /* todo----------------------------------------------------------------------------------*/



    // 외부 메소드: 결제건
    private Payment getPayment(Long orderId) {
        Payment payment = paymentRepository.findPaymentByOrderId(orderId).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_PAYMENT_NOT_FOUND));
        return payment;
    }

    // 외부 메소드: 소비자와 주문건의 소비자의 일치여부
    private static void isBuyerAndOrderBuyerSame(Buyer buyer, Order order) {
        if (!(order.getBuyer().getId() == buyer.getId())) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_MISMATCH_ORDER_AND_BUYER);
        }
    }

    // 외부 메소드: 주문건
    private Order getOrder(Long orderId) {
        Order order = orderRepository.findOrderByIdAndIsDeletedFalse(orderId).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NO_ORDER));
        return order;
    }

    // 외부 메소드: 소비자
    private Buyer getBuyer(User user) {
        Buyer buyer = buyerRepository.findBuyerByUserId(user.getId()).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NOT_BUYER));
        return buyer;
    }

    // 외부 메소드: 사장님
    private Seller getSeller(Order order) {
        Seller seller = orderRepository.findSellerByShopId(order.getShop().getId()).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_SHOP));
        return seller;
    }

    // 외부 메소드: isOrderValid
    private Order isOrderValid(User user, Long orderId) {
        Buyer buyer = getBuyer(user);
        Order order = getOrder(orderId);

        if (order.getBuyer().getId() == buyer.getId()) {
            return order;
        } else throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_MISMATCH_ORDER_AND_BUYER);
    }

    // 외부 메소드: 결제건 여부 체크
    private void isPaymentExistent(Order order) {
        Optional<Payment> optionalPayment = paymentRepository.findPaymentByOrderId(order.getId());
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get(); // 이미 생성되어 있는 결제건
            checkPaymentStatus(payment); // 결제 상태값이 ready 가 아닌 다른 것들 -> 에러
            paymentRepository.delete(payment); // 결제 상태값이 ready -> 삭제
        }
    }

    // 외부 메소드: 주문의 상태값 체크 (결제준비, 결제승인일 경우)
    private static void checkOrderStatus(Order order) throws CustomException {
        if (order.getOrderStatus().equals("PAID")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_PAID);
        } else if (order.getOrderStatus().equals("CANCELED")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_CANCELLED);
        } else if (order.getOrderStatus().equals("TAKEOVER")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_TAKEOVER);
        } else if (order.getOrderStatus().equals("DELIVERY")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_IN_DELIVERY);
        } else if (order.getOrderStatus().equals("COMPLETE")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_COMPLETED);
        } else if (order.getOrderStatus().equals("COOKING")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_COOKING);
        }
    }

    // 외부 메소드: 결제의 상태값 체크 (결제준비, 결제승인일 경우)
    private static void checkPaymentStatus(Payment payment) {
        if (payment.getPaymentStatus().equals("paid")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_PAID);
        } else if (payment.getPaymentStatus().equals("canceled")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_CANCELLED);
        }
    }


    // 외부 메소드: 주문의 상태값 체크 (결제후 취소일 경우)
    private static void checkOrderStatusAfterPay(Order order) throws CustomException {
        if (order.getOrderStatus().equals("WAIT")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_PAYMENT_NOT_FOUND);
        } else if (order.getOrderStatus().equals("CANCELED")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_CANCELLED);
        } else if (order.getOrderStatus().equals("TAKEOVER")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_TAKEOVER);
        } else if (order.getOrderStatus().equals("DELIVERY")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_IN_DELIVERY);
        } else if (order.getOrderStatus().equals("COMPLETE")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_COMPLETED);
        }
    }

    // 외부 메소드: 결제의 상태값 체크 (결제후 취소일 경우)
    private static void checkPaymentStatusAfterPay(Payment payment) {
        if (payment.getPaymentStatus().equals("ready")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_PAYMENT_NOT_FOUND);
        } else if (payment.getPaymentStatus().equals("canceled")) {
            throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_ALREADY_CANCELLED);
        }
    }

    // 외부 메소드: (아임포트) HttpHeaders 셋팅
    private static HttpHeaders setHttpHeaders(String access_token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Bearer " + access_token);
        return headers;
    }

    // 외부 메소드: (카카오서버) HttpHeaders 셋팅
    private HttpHeaders setKakaoHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", " KakaoAK " + kakaoKey);
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
        return headers;
    }

    // 외부 메소드: 해당 주문건의 카트들
    private List<Cart> getCarts(Long buyerId, Long orderId) {
        List<Cart> carts = emCartRepository.findCartsByPaidCartForOrderDetail(buyerId, orderId);
        if (carts.isEmpty()) {
            throw new CustomException(CartErrorCode.EMPTY_CART);
        }
        return carts;
    }

    // 외부 메소드: 주문명
    private static String getOrderName(List<Cart> carts, List<String> menuNames) {
        String orderName = IntStream.range(0, menuNames.size()).mapToObj(i -> (i == 0 ? menuNames.get(i) + " " + carts.get(i).getAmount() + "개" : "외 " + carts.stream().skip(1).mapToInt(cart -> cart.getAmount().intValue()).sum() + "개")).collect(Collectors.joining(" "));
        return orderName;
    }

    // 외부 메소드: 결제중 취소/실패
    private void paymentFailed(Long orderId, Order order) {
        // 주문건 상태값 변경
        order.setOrderStatus(OrderStatus.valueOf("CANCELED"));
        // 주문건 삭제
        order.setIsDeleted(true);
        orderRepository.save(order);
        // 주문건 카트들 가져오기(주문 아이디로)
        List<Cart> carts = emCartRepository.findCartsByPaidCartForPaymentCancel(orderId);
        if (carts.isEmpty()) {
            throw new CustomException(CartErrorCode.EMPTY_CART);
        }
        // 주문건 카트들 되살리기(isDeleted 상태값 변경)
        carts.forEach(cart -> {
            cart.setIsDeleted(false);
            cartRepository.save(cart);
        });
    }

    private void setOrderSequence(Order order) {
        Shop shop = order.getShop();
        List<Order> ordersOfShop = orderRepository.findByShopAndOrderStatus(shop, OrderStatus.PAID).orElse(null);
        Integer orderPosition = 0;
        if (ordersOfShop != null) {
            orderPosition = ordersOfShop.stream()
                    .max(Comparator.comparingInt(orderOfShop -> orderOfShop.getOrderSequence()))
                    .map(orderOfShop -> orderOfShop.getOrderSequence()).orElse(0);
        }

        order.setOrderSequence(orderPosition);
    }

}


package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.orderTest.ViewOrderDetailResponse;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.PaymentErrorCode;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.OrderTestRepository;
import com.supercoding.hanyipman.repository.PaymentRepository;
import com.supercoding.hanyipman.repository.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderTestService {

    private final OrderTestRepository orderTestRepository;
    private final BuyerRepository buyerRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final ShopRepository shopRepository;

    /* todo 메뉴명 필드 삽입 예정,
            예외 처리 부분 수정 예정,
            orderTest 번호 11번 이상(디버깅 해서 shop Id 18 분명 존재하는데 계속 존재하지 않는 가게라고 함)
    * */
    public ViewOrderDetailResponse viewOrderDetail(User user, Long orderId) throws ParseException {
        // 해당 주문
        OrderTest orderTest = isOrderValid(user, orderId);
        // 해당 주문의 주소(주소는 한 소비자당 여러개일 수 있으므로 주문건에 있는 주소아이디를 넣고 뽑아야)
        Address addressFound = addressRepository.findAddressById(orderTest.getAddressId());
        // 해당 주문의 가게
        Shop shopFound = shopRepository.findById(orderTest.getShopId()).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_SHOP));

//        // 주소에서 간단주소, 상세주소 추출
//        Map<String, String> address = new HashMap<>();
//        address.put("address", addressFound.getAddress());
//        address.put("detailAddress", addressFound.getDetailAddress());

        // 주소 = 간단주소 + 상세주소
        String address = String.join(" ", addressFound.getAddress(), addressFound.getDetailAddress());

        // 해당 결제건
        Payment payment = paymentRepository.findPaymentByOrderTestId(orderTest).orElseThrow(()-> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NOT_PAID_YET));

        return new ViewOrderDetailResponse().toDto(orderTest, payment, address, shopFound);
    }

    private OrderTest isOrderValid(User user, Long orderId) {
        Boolean areYouBuyer = buyerRepository.existsByUser(user);

        if (Boolean.TRUE.equals(areYouBuyer)) {
            Buyer buyer = buyerRepository.findByUser(user);

            // OrderTest를 orderId로 찾기
            OrderTest orderTest = orderTestRepository.findOrderTestById(orderId).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NO_ORDER));

            // 주문건의 소비자 아이디와 로그인한 소비자의 아이디가 같을 때만
            if (orderTest.getBuyerId().getId() == buyer.getId()) {
                return orderTest;

            } else throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_MISMATCH_ORDER_AND_BUYER);
        } else throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_NOT_BUYER);
    }
}

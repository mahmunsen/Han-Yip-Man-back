package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.orderTest.ViewOrderDetailResponse;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.*;
import com.supercoding.hanyipman.repository.*;
import com.supercoding.hanyipman.repository.cart.CartRepository;
import com.supercoding.hanyipman.repository.cart.EmCartRepository;
import com.supercoding.hanyipman.repository.shop.ShopRepository;
import com.supercoding.hanyipman.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.supercoding.hanyipman.utils.DateUtils.orderDatePattern;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final BuyerCouponRepository buyerCouponRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final EmCartRepository emCartRepository;
    private final BuyerRepository buyerRepository;
    private final CartOptionItemRepository cartOptionItemRepository;
    private final PaymentRepository paymentRepository;
    private final ShopRepository shopRepository;


    @Transactional
    public Long order(Long userId, Long addressId, Long buyerCouponId) {

        // 사용자 검증 및 주소 fetch join
        Buyer buyer = findBuyerByUserId(userId);

        // 주소 조회
        Address address = findAddressByAddressIdAndBuyer(addressId, buyer);

        // 쿠폰 조회   쿠폰 <-> 구매자쿠폰 fetch join
        BuyerCoupon coupon = null;
        if (buyerCouponId != null)
            coupon = buyerCouponRepository.findBuyerCouponFetchCouponByBuyerCouponId(buyerCouponId).orElseThrow(() -> new CustomException(BuyerCouponError.NOT_FOUND_BUYER_COUPON));

        // 장바구니들 가져오기 items제외 전부 fetch join
        List<Cart> carts = emCartRepository.findCartsByUnpaidCart(buyer.getId());

        // 주문할 장바구니 없을 시 예외 처리
        if (carts.isEmpty()) throw new CustomException(CartErrorCode.EMPTY_CART);

        // carts <-> cartOptionItems 연결
        List<Cart> cartsJoinItems = getCartsJoinItems(carts);

        // 주문 uid 생성
        String orderUid = generateOrderUid();

        // 주문 엔티티 생성 후 저장  // 장바구니 <-> 주문 연결 
        Order order = Order.from(buyer, orderUid, address, carts.get(0).getShop(), coupon, cartsJoinItems);
        orderRepository.save(order); //TODO: 주문 상세보기에서 할인된 금액이 필요할 경우 변경될 수 있음

        // 주문 식별값 ID 반환
        return order.getId();
    }

    private List<Cart> getCartsJoinItems(List<Cart> carts) {
        List<Long> cartIds = carts.stream().map(Cart::getId).collect(Collectors.toList());
        List<CartOptionItem> cartOptionItems = cartOptionItemRepository.findCartOptionItemsByCartIds(cartIds);
        Map<Long, List<CartOptionItem>> coiMap = cartOptionItems.stream().collect(Collectors.groupingBy(coi -> coi.getCart().getId()));
        carts.forEach(cart -> cart.setCartOptionItems(coiMap.get(cart.getId())));
        return carts;
    }

    private Address findAddressByAddressIdAndBuyer(Long addressId, Buyer buyer) {
        return addressRepository.findByBuyerAndId(buyer, addressId).orElseThrow(() -> new CustomException(AddressErrorCode.EMPTY_ADDRESS_DATA));
    }

    public String generateOrderUid() {
        String orderDate = DateUtils.convertToString(Instant.now(), orderDatePattern);
        String uid = UUID.randomUUID().toString().substring(0, 8);
        return String.join("", orderDate, uid);
    }

    private Buyer findBuyerByUserId(Long userId) {
        return buyerRepository.findBuyerByUserId(userId).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }





    /* todo 메뉴명 필드 삽입 예정,
      예외 처리 부분 수정 예정,
      orderTest 번호 11번 이상(디버깅 해서 shop Id 18 분명 존재하는데 계속 존재하지 않는 가게라고 함)*/
    public ViewOrderDetailResponse viewOrderDetail(User user, Long orderId) throws ParseException {
        // 해당 주문
        Order order = isOrderValid(user, orderId);
        Address addressFound = addressRepository.findAddressById(order.getAddress().getId());
        // 해당 주문의 가게
        Shop shopFound = shopRepository.findById(order.getShop().getId()).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_SHOP));

//        // 주소에서 간단주소, 상세주소 추출
//        Map<String, String> address = new HashMap<>();
//        address.put("address", addressFound.getAddress());
//        address.put("detailAddress", addressFound.getDetailAddress());

        // 주소 = 간단주소 + 상세주소
        String address = String.join(" ", addressFound.getAddress(), addressFound.getDetailAddress());

        // 해당 결제건
        Payment payment = paymentRepository.findPaymentByOrder(order).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_PAYMENT_NOT_FOUND));

        return new ViewOrderDetailResponse().toDto(order, payment, address, shopFound);
    }


    /* todo 외부메소드 */
    private Order isOrderValid(User user, Long orderId) {
        Boolean areYouBuyer = buyerRepository.existsByUser(user);

        if (Boolean.TRUE.equals(areYouBuyer)) {
            Buyer buyer = buyerRepository.findByUser(user);

            // Order를 orderId로 찾기
            Order order = orderRepository.findOrderById(orderId).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_NO_ORDER));

            // 주문건의 소비자 아이디와 로그인한 소비자의 아이디가 같을 때만
            if (order.getBuyer().getId() == buyer.getId()) {
                return order;

            } else throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_MISMATCH_ORDER_AND_BUYER);
        } else throw new CustomException(PaymentErrorCode.PAYMENT_COMMON_NOT_BUYER);
    }
}

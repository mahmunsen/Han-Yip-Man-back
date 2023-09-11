package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.AddressErrorCode;
import com.supercoding.hanyipman.error.domain.BuyerCouponError;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.error.domain.CartErrorCode;
import com.supercoding.hanyipman.repository.*;
import com.supercoding.hanyipman.repository.cart.CartRepository;
import com.supercoding.hanyipman.repository.cart.EmCartRepository;
import com.supercoding.hanyipman.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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



    @Transactional
    public Long order(Long userId, Long addressId, Long buyerCouponId) {

        // 사용자 검증 및 주소 fetch join
        Buyer buyer = findBuyerByUserId(userId);

        // 주소 조회
        Address address = findAddressByAddressIdAndBuyer(addressId, buyer);

        // 쿠폰 조회   쿠폰 <-> 구매자쿠폰 fetch join
        BuyerCoupon coupon = null;
        if(buyerCouponId != null)
            coupon = buyerCouponRepository.findBuyerCouponFetchCouponByBuyerCouponId(buyerCouponId).orElseThrow(() -> new CustomException(BuyerCouponError.NOT_FOUND_BUYER_COUPON));

        // 장바구니들 가져오기 items제외 전부 fetch join
        List<Cart> carts = emCartRepository.findCartsByUnpaidCart(buyer.getId());

        // 주문할 장바구니 없을 시 예외 처리
        if(carts.isEmpty()) throw new CustomException(CartErrorCode.EMPTY_CART);

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

}

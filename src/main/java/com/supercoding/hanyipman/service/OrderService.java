package com.supercoding.hanyipman.service;
import com.supercoding.hanyipman.dto.order.response.OrderNoticeResponse;
import com.supercoding.hanyipman.dto.order.response.ViewOrderDetailResponse;
import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.order.response.ViewOrderResponse;
import com.supercoding.hanyipman.dto.vo.CustomPageable;
import com.supercoding.hanyipman.dto.vo.PageResponse;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.enums.OrderStatus;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.*;
import com.supercoding.hanyipman.repository.*;
import com.supercoding.hanyipman.repository.cart.CartRepository;
import com.supercoding.hanyipman.repository.cart.EmCartRepository;
import com.supercoding.hanyipman.repository.order.EmOrderRepository;
import com.supercoding.hanyipman.repository.order.OrderRepository;
import com.supercoding.hanyipman.security.UserRole;
import com.supercoding.hanyipman.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.supercoding.hanyipman.utils.DateUtils.orderDatePattern;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final BuyerCouponRepository buyerCouponRepository;
    private final SellerRepository sellerRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final EmOrderRepository emOrderRepository;
    private final EmCartRepository emCartRepository;
    private final CartRepository cartRepository;
    private final BuyerRepository buyerRepository;
    private final CartOptionItemRepository cartOptionItemRepository;
    private final PaymentRepository paymentRepository;


    @TimeTrace
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
        List<Cart> cartsJoinItems = findCartsJoinItems(carts);

        // 주문 uid 생성
        String orderUid = generateOrderUid();

        // 주문 엔티티 생성 후 저장  // 장바구니 <-> 주문 연결 
        Order order = Order.from(buyer, orderUid, address, carts.get(0).getShop(), coupon, cartsJoinItems);
        orderRepository.save(order); //TODO: 주문 상세보기에서 할인된 금액이 필요할 경우 변경될 수 있음

        // 주문 식별값 ID 반환
        return order.getId();
    }

    @TimeTrace
    public PageResponse<ViewOrderResponse> findOrders(Long userId, CustomPageable pageable) {
        Buyer buyer = findBuyerByUserId(userId);

        List<Order> orders = emOrderRepository.findListOrders(buyer.getId(), pageable);
        List<Long> ordersId = orders.stream().map(Order::getId).collect(Collectors.toList());
        List<Cart> cartsJoinItems = findCartsJoinItems(cartRepository.findCartsByOrdersId(ordersId));
        Map<Long, List<Cart>> cartsMap = cartsJoinItems.stream().collect(Collectors.groupingBy(cart -> cart.getOrder().getId()));
        orders.forEach(order -> order.setCarts(cartsMap.get(order.getId())));

        List<ViewOrderResponse> viewOrderResponses = orders.stream().map(ViewOrderResponse::from).collect(Collectors.toList());
        calcCursorIdx(pageable, orders);
        return PageResponse.from(viewOrderResponses, pageable);
    }

    @TimeTrace
    protected OrderNoticeResponse findOrder(Long userId, Long orderId) {
        Buyer buyer = findBuyerByUserId(userId);

        Order order = findOrderByOrderId(orderId);
        List<Cart> carts = emCartRepository.findCartsByPaidCartForOrderDetail(buyer.getId(), orderId);
        List<Cart> joinItems = findCartsJoinItems(carts);
        order.setCarts(joinItems);

        return OrderNoticeResponse.from(order);
    }




    public String generateOrderUid() {
        String orderDate = DateUtils.convertToString(Instant.now(), orderDatePattern);
        String uid = UUID.randomUUID().toString().substring(0, 8);
        return String.join("", orderDate, uid);
    }


    /**
     * 주문 수락 메소드
     * @param orderId
     * @param userId
     */
    public void approveOrder(Long orderId, Long userId) {
        Seller seller = findSellerByUserId(userId);
        Order order = findOrderByOrderId(orderId);
        isSameSellerIdAndOrderShopSellerId(order, seller);
        OrderStatus.isEqualCancel(order.getOrderStatus());
        order.setOrderStatus(OrderStatus.TAKEOVER);
    }

    /**
     * 주문 상태 변경 메소드
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(Long orderId, Long userId, OrderStatus orderStatus) {
        Order order = findOrderByOrderId(orderId);
        Seller seller = findSellerByUserId(userId);
        isSameSellerIdAndOrderShopSellerId(order, seller);
        OrderStatus.isEqualCancel(orderStatus);
        order.setOrderStatus(orderStatus);
    }


    /**
     * 사장님, 손님이 의도적인 취소 메소드
     * @param orderId
     * @param userId
     */
    public void IntentionalCancelOrder(Long orderId, Long userId){
        User user = findUserByUserId(userId);
        Order order = findOrderByOrderId(orderId);
        OrderStatus.isPossibleCancel(order.getOrderStatus());
        if(user.getRole().equals(UserRole.SELLER.name())){
            IntentionalCancelOrderBySeller(order, userId);
            return ;
        }
        IntentionalCancelOrderByBuyer(order, userId);
    }

    private static void calcCursorIdx(CustomPageable pageable, List<Order> orders) {
        if(orders.size() > 0) pageable.setCursor(orders.get(orders.size() - 1).getId());
    }

    private List<Cart> findCartsJoinItems(List<Cart> carts) {
        List<Long> cartIds = carts.stream().map(Cart::getId).collect(Collectors.toList());
        List<CartOptionItem> cartOptionItems = findCartOptionItemsByCartsId(cartIds);
        Map<Long, List<CartOptionItem>> coiMap = cartOptionItems.stream().collect(Collectors.groupingBy(coi -> coi.getCart().getId()));
        carts.forEach(cart -> cart.setCartOptionItems(coiMap.get(cart.getId())));
        return carts;
    }

    private List<CartOptionItem> findCartOptionItemsByCartsId(List<Long> cartIds) {
        List<CartOptionItem> cartOptionItems = cartOptionItemRepository.findCartOptionItemsByCartIds(cartIds);
        return cartOptionItems;
    }

    private Address findAddressByAddressIdAndBuyer(Long addressId, Buyer buyer) {
        return addressRepository.findByBuyerAndId(buyer, addressId).orElseThrow(() -> new CustomException(AddressErrorCode.EMPTY_ADDRESS_DATA));
    }

    private Buyer findBuyerByUserId(Long userId) {
        return buyerRepository.findBuyerByUserId(userId).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }

    private void isSameBuyerIdAndOrderBuyerId(Order order, Buyer buyer) {
        if(!order.getBuyer().getId().equals(buyer.getId())) throw new CustomException(OrderErrorCode.NOT_SAME_ORDER_BUYER);
    }

    private Order findOrderFetchBuyer(Long orderId) {
        return orderRepository.findOrderFetchBuyerByOrderId(orderId).orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private Order findOrderByOrderId(Long orderId) {
        return orderRepository.findOrderById(orderId).orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private void IntentionalCancelOrderByBuyer(Order order, Long userId) {
        Buyer buyer = findBuyerByUserId(userId);
        isSameBuyerIdAndOrderBuyerId(order, buyer);
        order.setOrderStatus(OrderStatus.CANCELED);
    }

    private void IntentionalCancelOrderBySeller(Order order, Long userId) {
        Seller seller = findSellerByUserId(userId);
        isSameSellerIdAndOrderShopSellerId(order, seller);
        order.setOrderStatus(OrderStatus.CANCELED);
    }

    private Seller findSellerByUserId(Long userId) {
        return sellerRepository.findByUserId(userId).orElseThrow(() -> new CustomException(OrderErrorCode.NOT_SAME_ORDER_SELLER));
    }

    private void isSameSellerIdAndOrderShopSellerId(Order order, Seller seller) {
        // TODO: 최적화 가능
        if (!order.getShop().getSeller().getId().equals(seller.getId()))
            throw new CustomException(OrderErrorCode.NOT_SAME_SHOP_SELLER);
    }

    private User findUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
    }

    /**
     * 결제된 이후 해당 주문건 상세페이지 조회
     */
//    @Cacheable(value = "viewOrderDetail", keyGenerator = "cacheKeyGenerator")
    @Transactional
    public ViewOrderDetailResponse viewOrderDetail(User user, Long orderId) throws ParseException {
        // 해당 주문건의 소비자
        Buyer buyer = findBuyerByUserId(user.getId());
        // 해당 주문건, orderId로 찾기(삭제안된 것만)
        Order order = getOrder(orderId);

        isSameBuyerIdAndOrderBuyerId(order, buyer);

        // 해당 주문건의 카트들 가져오기(소비자 아이디와 주문 아이디로)
        List<Cart> carts = emCartRepository.findCartsByPaidCartForOrderDetail(buyer.getId(), orderId);
        if (carts.isEmpty()) {
            throw new CustomException(CartErrorCode.EMPTY_CART);
        }
        // 카트들에서 해당 메뉴들 불러오기
        List<Map<String, Object>> orderMenus = carts.stream().map(cart -> {
            Map<String, Object> orderMenu = getOrderMenu(cart);
            return orderMenu;
        }).collect(Collectors.toList());

        // 메뉴이름들
        List<String> menuNames = carts.stream().map(cart -> cart.getMenu().getName()).collect(Collectors.toList());

        // 주문명
        String orderName = createOrderName(carts, menuNames);

        // 주소 = 간단주소 + 상세주소
        String address = String.join(" ", order.getAddress().getAddress(), order.getAddress().getDetailAddress());

        // 해당 결제건
        Payment payment = paymentRepository.findPaymentByOrderId(order.getId()).orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_COMMON_PAYMENT_NOT_FOUND));

        return new ViewOrderDetailResponse().toDto(order, payment, address, order.getShop(), orderMenus, orderName);
    }

    // 주문건
    private Order getOrder(Long orderId) {
        Order order = orderRepository.findOrderByIdAndIsDeletedFalse(orderId).orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
        return order;
    }

    // 메뉴들
    private static Map<String, Object> getOrderMenu(Cart cart) {
        Map<String, Object> orderMenu = new HashMap<>();
        Menu menu = cart.getMenu();
        // 주문명: 메뉴명 + 수량
        String menuNameAndAmount = menu.getName() + " x " + cart.getAmount();
        // 메뉴가격: 메뉴 가격 * 수량
        Integer menuPrice = menu.getPrice() * cart.getAmount().intValue();
        // 메뉴옵션들
        List<Map<String, Object>> options = getMenuOptions(cart);

        orderMenu.put("name", menuNameAndAmount);
        orderMenu.put("price", menuPrice);
        orderMenu.put("options", options);
        return orderMenu;
    }

    // 메뉴옵션들
    private static List<Map<String, Object>> getMenuOptions(Cart cart) {
        List<Map<String, Object>> options = cart.getCartOptionItems().stream().map(cartOptionItem -> {
            OptionItem optionItem = cartOptionItem.getOptionItem();
            Map<String, Object> optionInfo = new HashMap<>();
            optionInfo.put("optionName", optionItem.getName() + " x " + cart.getAmount());
            optionInfo.put("optionPrice", optionItem.getPrice() * cart.getAmount().intValue());
            return optionInfo;
        }).collect(Collectors.toList());
        return options;
    }

    // 메뉴이름들 가지고 주문명 만들기
    private static String createOrderName(List<Cart> carts, List<String> menuNames) {
        String orderName = IntStream.range(0, menuNames.size()).mapToObj(i -> (i == 0 ? menuNames.get(i) + " " + carts.get(i).getAmount() + "개" : "외 " + carts.stream().skip(1).mapToInt(cart -> cart.getAmount().intValue()).sum() + "개")).collect(Collectors.joining(" "));
        return orderName;
    }
}
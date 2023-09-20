package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.cart.response.OptionItemResponse;
import com.supercoding.hanyipman.dto.cart.response.ViewCartResponse;
import com.supercoding.hanyipman.dto.vo.CustomPageable;
import com.supercoding.hanyipman.dto.vo.PageResponse;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.*;
import com.supercoding.hanyipman.repository.*;
import com.supercoding.hanyipman.repository.menu.MenuCustomRepositoryImpl;
import com.supercoding.hanyipman.repository.menu.MenuRepository;
import com.supercoding.hanyipman.repository.shop.ShopRepository;
import com.supercoding.hanyipman.repository.cart.CartRepository;
import com.supercoding.hanyipman.repository.cart.EmCartRepository;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class CartService {
    private final BuyerRepository buyerRepository;
    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final OptionItemRepository optionItemRepository;
    private final CartOptionItemRepository cartOptionItemRepository;
    private final CartRepository cartRepository;
    private final EmCartRepository emCartRepository;
    private final MenuCustomRepositoryImpl menuCustomRepository;

    @TimeTrace
    @Transactional
    public void registerCart(Long userId, Long shopId, Long menuId, List<Long> optionsId, Long amount) {
        // 구매자 찾기
        Buyer buyer = findBuyerByUserId(userId); //TODO: query 개선할 것
        // 기존 담은 장바구니 가져오기
        List<Cart> carts = cartRepository.findCartsByBuyerId(buyer.getId());
        // 기존 장바구니와 다른 가게인지 확인
        carts.stream().filter(cart -> !cart.getShop().getId().equals(shopId))
                .findAny()
                .ifPresent((i) -> {throw new CustomException(ShopErrorCode.DIFFERENT_SHOP);});
        // 가게 찾기
        Shop shop = shopRepository.findShopByShopId(shopId).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_SHOP));
        // 담으려는 메뉴 찾기
        Menu menu = menuCustomRepository.findMenuByShop(shop.getId(), menuId).orElseThrow(() -> new CustomException(MenuErrorCode.NOT_FOUND_MENU));
        // 장바구니 생성
        Cart cart = Cart.from(buyer, shop, menu, amount);
        // 생성된 엔티티 저장
        cartRepository.save(cart);
        // 선택한 옵션 아이템 찾기
        //옵션 유효성 검사
        // 장바구니에 <-> 옵션 아이템 중간객체 생성
        // 생성된 엔티티 저장
        createCartOptionsItem(optionsId, cart);
    }

    private void createCartOptionsItem(List<Long> optionsId, Cart cart) {
        if(optionsId != null){
            List<OptionItem> optionItems = optionItemRepository.findByOptionItemIds(optionsId);
            isValidOptions(optionsId.stream().sorted().collect(Collectors.toList()), optionItems);
            List<CartOptionItem> cartOptionItems = createCartOptionItems(cart, optionItems);
            cartOptionItemRepository.saveAll(cartOptionItems);
        }
    }

    private static void isValidOptions(List<Long> options, List<OptionItem> optionItems) {
        int optionsSize = options.size();
        int findOptionsSize = optionItems.size();
        // 전달받은 옵션들과 찾은 옵션 갯수 체크
        if (optionsSize != findOptionsSize) throw new CustomException(OptionErrorCode.NOT_MATCH_OPTION_AMOUNT);
        // 옵션 번호 일치하는지 확인
        IntStream.range(0, optionsSize).forEach(i -> {
            if (!Objects.equals(optionItems.get(i).getId(), options.get(i))) throw new CustomException(OptionErrorCode.OPTION_NOT_FOUND);
        });
    }

    private List<CartOptionItem> createCartOptionItems(Cart cart, List<OptionItem> optionItems) {
        return optionItems.stream()
                .map(coi -> CartOptionItem.from(coi, cart))
                .collect(Collectors.toList());
    }

    private Menu findMenuByMenuId(Long menuId) {
        return menuRepository.findMenuByMenuId(menuId).orElseThrow(() -> new CustomException(MenuErrorCode.NOT_FOUND_MENU));
    }

    private Buyer findBuyerByUserId(Long userId) {
        return buyerRepository.findBuyerByUserId(userId).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }


    @Transactional(readOnly = true)
    public Page<ViewCartResponse> findUnpaidCartsAndOptionItemsV1(Pageable pageable) {
        //구매자 찾기
        Buyer buyer = findBuyerByUserId(JwtToken.user().getId());
        //coptions, totalPrice 제외 가져오기
        Page<ViewCartResponse> carts = cartRepository.findCartsByUnpaidCart(buyer.getId(), pageable).map(ViewCartResponse::from);

        //Cart Ids추출
        List<Long> cartIds = carts.getContent().stream().map(ViewCartResponse::getCartId).collect(Collectors.toList());

        //IDS로 optionItems 추출 후 Map 변환
        List<CartOptionItem> cartOptionItems = cartOptionItemRepository.findCartOptionItemsByCartIds(cartIds);

        Map<Long, List<OptionItemResponse>> coiMap = cartOptionItems.stream().map(OptionItemResponse::from)
                .collect(Collectors.groupingBy(OptionItemResponse::getCartId));

        //Carts에 주입하기
        carts.getContent().forEach(cart -> cart.setOptionItems(coiMap.get(cart.getCartId())));
        carts.forEach(ViewCartResponse::calTotalPrice);

        return carts;
    }
    @TimeTrace
    @Transactional(readOnly = true)
    public PageResponse<ViewCartResponse> findUnpaidCartsAndOptionItemsV2(CustomPageable pageable) {
        //구매자 찾기
        Buyer buyer = findBuyerByUserId(JwtToken.user().getId());
        //options, totalPrice 제외 가져오기
        List<ViewCartResponse> carts = emCartRepository.findPageableCartsByUnpaidCart(buyer.getId(), pageable).stream().map(ViewCartResponse::from).collect(Collectors.toList());
        calCursorIdx(pageable, carts);

        //Cart Ids추출
        List<Long> cartIds = carts.stream().map(ViewCartResponse::getCartId).collect(Collectors.toList());

        //IDS로 optionItems 추출 후 Map 변환
        List<CartOptionItem> cartOptionItems = cartOptionItemRepository.findCartOptionItemsByCartIds(cartIds);

        Map<Long, List<OptionItemResponse>> coiMap = cartOptionItems.stream().map(OptionItemResponse::from)
                .collect(Collectors.groupingBy(OptionItemResponse::getCartId));

        //Carts에 주입하기
        carts.forEach(cart -> cart.setOptionItems(coiMap.get(cart.getCartId())));
        carts.forEach(ViewCartResponse::calTotalPrice);

        return PageResponse.from(carts, pageable);
    }

    private static void calCursorIdx(CustomPageable pageable, List<ViewCartResponse> carts) {
        if(carts.size() >= 1) pageable.setCursor(carts.get(carts.size()-1).getCartId());
    }

    @Transactional
    public void updateCart(Long cartId, Long amount, Long userId) {
        findBuyerByUserId(userId);
        Cart cart = getCart(cartId);
        cart.setAmount(amount);
    }

    private Cart getCart(Long cartId) {
        return cartRepository.findCartByCartId(cartId)
                .orElseThrow(() -> new CustomException(CartErrorCode.NOT_FOUND_CART));
    }

    @Transactional
    public void deleteCart(Long userId, Long cartId) {
        Buyer buyer = findBuyerByUserId(userId);
        Cart cart = getCart(cartId);
        if(!buyer.equals(cart.getBuyer())) throw new CustomException(BuyerErrorCode.NOT_SAME_BUYER);

        cartRepository.delete(cart);
    }

    @Transactional
    public void deleteCarts(Long userId) {
        Buyer buyer = findBuyerByUserId(userId);
        List<Cart> carts = cartRepository.findCartsByBuyerId(buyer.getId());
        cartRepository.deleteAll(carts);
    }
}

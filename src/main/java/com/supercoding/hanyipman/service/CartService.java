package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.cart.request.RegisterCartRequest;
import com.supercoding.hanyipman.dto.cart.response.OptionItemResponse;
import com.supercoding.hanyipman.dto.cart.response.ViewCartResponse;
import com.supercoding.hanyipman.dto.vo.CustomPageable;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.error.domain.MenuErrorCode;
import com.supercoding.hanyipman.error.domain.OptionErrorCode;
import com.supercoding.hanyipman.repository.*;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final MenuRepository menuRepository;
    private final OptionItemRepository optionItemRepository;
    private final CartOptionItemRepository cartOptionItemRepository;
    private final CartRepository cartRepository;
    private final EmCartRepository emCartRepository;

    @TimeTrace
    @Transactional
    public void registerCart(Long userId, RegisterCartRequest request) {
        // 구매자 찾기
        Buyer buyer = findBuyerByUserId(userId);
        // 담으려는 메뉴 찾기
        Menu menu = findMenuByMenuId(request);
        // 장바구니 생성
        Cart cart = Cart.from(buyer, menu, request.getAmount());
        // 생성된 엔티티 저장
        cartRepository.save(cart);
        // 선택한 옵션 아이템 찾기
        List<OptionItem> optionItems = optionItemRepository.findByOptionItemIds(request.getOptions());
        //옵션 유효성 검사
        isValidOptions(request.getOptions().stream().sorted().collect(Collectors.toList()), optionItems);
        // 장바구니에 <-> 옵션 아이템 중간객체 생성
        List<CartOptionItem> cartOptionItems = createCartOptionItems(cart, optionItems);
        // 생성된 엔티티 저장
        cartOptionItemRepository.saveAll(cartOptionItems);
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

    private Menu findMenuByMenuId(RegisterCartRequest request) {
        return menuRepository.findMenuByMenuId(request.getMenuId()).orElseThrow(() -> new CustomException(MenuErrorCode.NOT_FOUND_MENU));
    }

    private Buyer findBuyerByUserId(Long userId) {
        return buyerRepository.findBuyerByUserId(userId).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }


    @Transactional(readOnly = true)
    public Page<ViewCartResponse> findUnpaidCartsV1(Pageable pageable) {
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
    public List<ViewCartResponse> findUnpaidCartsV2(CustomPageable pageable) {
        //구매자 찾기
        Buyer buyer = findBuyerByUserId(JwtToken.user().getId());
        //options, totalPrice 제외 가져오기
        List<ViewCartResponse> carts = emCartRepository.findCartsByUnpaidCart(buyer.getId(), pageable).stream().map(ViewCartResponse::from).collect(Collectors.toList());

        //Cart Ids추출
        List<Long> cartIds = carts.stream().map(ViewCartResponse::getCartId).collect(Collectors.toList());

        //IDS로 optionItems 추출 후 Map 변환
        List<CartOptionItem> cartOptionItems = cartOptionItemRepository.findCartOptionItemsByCartIds(cartIds);

        Map<Long, List<OptionItemResponse>> coiMap = cartOptionItems.stream().map(OptionItemResponse::from)
                .collect(Collectors.groupingBy(OptionItemResponse::getCartId));

        //Carts에 주입하기
        carts.forEach(cart -> cart.setOptionItems(coiMap.get(cart.getCartId())));
        carts.forEach(ViewCartResponse::calTotalPrice);

        return carts;
    }
}

package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.cart.request.RegisterCartRequest;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.error.domain.MenuErrorCode;
import com.supercoding.hanyipman.error.domain.OptionErrorCode;
import com.supercoding.hanyipman.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        cartOptionItemRepository.saveCartOptionItems(cartOptionItems);
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
}

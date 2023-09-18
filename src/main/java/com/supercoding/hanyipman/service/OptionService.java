package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.option.request.RegisterOptionItemRequest;
import com.supercoding.hanyipman.dto.option.request.RegisterOptionRequest;
import com.supercoding.hanyipman.entity.Menu;
import com.supercoding.hanyipman.entity.Option;
import com.supercoding.hanyipman.entity.OptionItem;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.MenuErrorCode;
import com.supercoding.hanyipman.error.domain.OptionErrorCode;
import com.supercoding.hanyipman.error.domain.OptionItemErrorCode;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.repository.MenuRepository;
import com.supercoding.hanyipman.repository.OptionItemRepository;
import com.supercoding.hanyipman.repository.OptionRepository;
import com.supercoding.hanyipman.repository.shop.ShopCustomRepository;
import com.supercoding.hanyipman.repository.shop.ShopCustomRepositoryImpl;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final MenuRepository menuRepository;
    private final ShopCustomRepositoryImpl shopCustomRepository;
    private final OptionItemRepository optionItemRepository;

    @Transactional
    public void registerOption(RegisterOptionRequest registerOptionRequest, Long menuId) {
        Seller seller = JwtToken.user().validSeller();
        Menu menu = validMenu(menuId);
        menu.getMenuGroup().getShop().checkRegisterSeller(seller.getId());

        menu.addOption(Option.builder()
                        .name(registerOptionRequest.getOptionName())
                        .isMultiple(registerOptionRequest.getIsMultiple())
                        .maxSelected(registerOptionRequest.getMaxSelected())
                        .isDeleted(false)
                        .build());
    }

    @Transactional
    public void registerOptionItem(RegisterOptionItemRequest registerOptionItemRequest, Long optionId) {

        Seller seller = JwtToken.user().validSeller();
        Option option = validOption(optionId);
        checkShopRegisterOption(optionId, seller.getId());

        option.addOption(OptionItem.builder()
                .name(registerOptionItemRequest.getName())
                .price(registerOptionItemRequest.getPrice())
                .isDeleted(false)
                .build()
        );
    }

    public void changeOptionItem(RegisterOptionItemRequest registerOptionItemRequest, Long optionItemId) {
        Seller seller = JwtToken.user().validSeller();
        OptionItem originOptionItem = validOptionItem(optionItemId);
        checkShopRegisterOptionItem(optionItemId, seller.getId());
        OptionItem newOptionItem = OptionItem.builder()
                .id(originOptionItem.getId())
                .option(originOptionItem.getOption())
                .name(registerOptionItemRequest.getName())
                .price(registerOptionItemRequest.getPrice())
                .isDeleted(false)
                .build();
        optionItemRepository.save(newOptionItem);
    }

    public void changeOption(RegisterOptionRequest registerOptionRequest, Long optionId) {
        Seller seller = JwtToken.user().validSeller();
        Option originOption = optionRepository.findByIdAndIsDeletedFalse(optionId).orElseThrow(()->new CustomException(OptionErrorCode.OPTION_NOT_FOUND));
        checkShopRegisterOption(optionId, seller.getId());

        Option newOption = Option.builder()
                            .id(originOption.getId())
                            .name(registerOptionRequest.getOptionName())
                            .isMultiple(registerOptionRequest.getIsMultiple())
                            .maxSelected(registerOptionRequest.getMaxSelected())
                            .menu(originOption.getMenu())
                            .optionItems(originOption.getOptionItems())
                            .isDeleted(false)
                            .build();
        optionRepository.save(newOption);
    }

    public void changeOptionItemByOption(Long optionItemId, Long optionId) {

        Seller seller = JwtToken.user().validSeller();
        Option option = validOption(optionId);
        OptionItem optionItem = validOptionItem(optionItemId);
        checkShopRegisterOptionItem(optionItemId, seller.getId());
        optionItem.setOption(option);
        optionItemRepository.save(optionItem);
    }

    private Menu validMenu(Long menuId) {
        return menuRepository.findByMenuIsDeletedFalse(menuId).orElseThrow(() -> new CustomException(MenuErrorCode.NOT_FOUND_MENU));
    }

    private Option validOption(Long optionId) {
        return optionRepository.findByIdAndIsDeletedFalse(optionId).orElseThrow(()->new CustomException(OptionErrorCode.OPTION_NOT_FOUND));
    }

    private OptionItem validOptionItem(Long optionItemId) {
        return optionItemRepository.findByIdAndIsDeletedFalse(optionItemId).orElseThrow(() -> new CustomException(OptionItemErrorCode.NOT_FOUND_OPTION_ITEM));
    }

    private void checkShopRegisterOption(Long optionId, Long sellerId) {
        if (!shopCustomRepository.checkRegisterShopSellerByOption(optionId, sellerId))
            throw new CustomException(ShopErrorCode.DIFFERENT_SELLER);
    }

    private void checkShopRegisterOptionItem(Long optionItemId, Long sellerId) {
        if (!shopCustomRepository.checkRegisterShopSellerByOptionItem(optionItemId, sellerId))
            throw new CustomException(ShopErrorCode.DIFFERENT_SELLER);
    }
}

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
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.repository.MenuRepository;
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
        checkShopRegister(optionId, seller.getId());

        option.addOption(OptionItem.builder()
                .name(registerOptionItemRequest.getName())
                .price(registerOptionItemRequest.getPrice())
                .isDeleted(false)
                .build()
        );

    }

    public void changeOption(RegisterOptionRequest registerOptionRequest, Long optionId) {
        Option originOption = optionRepository.findByIdAndIsDeletedFalse(optionId).orElseThrow(()->new CustomException(OptionErrorCode.OPTION_NOT_FOUND));
        Seller seller = JwtToken.user().validSeller();
        checkShopRegister(optionId, seller.getId());

        Option newOption = Option.builder()
                            .id(originOption.getId())
                            .name(registerOptionRequest.getOptionName())
                            .isMultiple(registerOptionRequest.getIsMultiple())
                            .maxSelected(registerOptionRequest.getMaxSelected())
                            .isDeleted(false)
                            .build();
        optionRepository.save(newOption);
    }

    private Menu validMenu(Long menuId) {
        return menuRepository.findByMenuIsDeletedFalse(menuId).orElseThrow(() -> new CustomException(MenuErrorCode.NOT_FOUND_MENU));
    }

    private Option validOption(Long optionId) {
        return optionRepository.findByIdAndIsDeletedFalse(optionId).orElseThrow(()->new CustomException(OptionErrorCode.OPTION_NOT_FOUND));
    }

    private void checkShopRegister(Long optionId, Long sellerId) {
        if (!shopCustomRepository.checkRegisterShopSellerByOption(optionId, sellerId))
            throw new CustomException(ShopErrorCode.DIFFERENT_SELLER);
    }

}

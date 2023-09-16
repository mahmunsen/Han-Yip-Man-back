package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.option.request.RegisterOptionRequest;
import com.supercoding.hanyipman.entity.Menu;
import com.supercoding.hanyipman.entity.Option;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.MenuErrorCode;
import com.supercoding.hanyipman.error.domain.OptionErrorCode;
import com.supercoding.hanyipman.repository.MenuRepository;
import com.supercoding.hanyipman.repository.OptionRepository;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final MenuRepository menuRepository;

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

    public void changeOptionName(String optionName, Long optionId) {
        Option option = optionRepository.findByIdAndIsDeletedFalse(optionId).orElseThrow(()->new CustomException(OptionErrorCode.OPTION_NOT_FOUND));
        option.setName(optionName);
        optionRepository.save(option);
    }

    private Menu validMenu(Long menuId) {
        return menuRepository.findByMenuIsDeletedFalse(menuId).orElseThrow(() -> new CustomException(MenuErrorCode.NOT_FOUND_MENU));
    }


}

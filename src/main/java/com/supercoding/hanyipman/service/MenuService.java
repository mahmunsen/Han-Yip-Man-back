package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuRequest;
import com.supercoding.hanyipman.dto.Shop.seller.response.MenuGroupResponse;
import com.supercoding.hanyipman.repository.MenuRepository;
import com.supercoding.hanyipman.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuGroupResponse menuGroupResponse;
    private final MenuRepository menuRepository;
    private final OptionRepository optionRepository;


    public void createMenu(RegisterMenuRequest registerMenuRequest, Long menuGroupId) {



    }
}

package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.entity.MenuGroup;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.entity.Shop;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.SellerErrorCode;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.repository.MenuGroupRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.shop.ShopRepository;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MenuGroupService {

    private final SellerRepository sellerRepository;
    private final ShopRepository shopRepository;
    private final MenuGroupRepository menuGroupRepository;

    public void createMenuGroup(String menuGroupName, Long shopId) {

        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        Integer sequence = menuGroupRepository.findMaxSequenceByShop(shop);

        menuGroupRepository.save(MenuGroup.from(shop, menuGroupName, sequence));
    }

    private Seller validSellerUser(User user) {
        return sellerRepository.findByUser(user).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
    }

    private Shop validShop(Long shopId, Long sellerId) {
        Shop shop = shopRepository.findShopByShopId(shopId).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_SHOP));
        if (!Objects.equals(shop.getSeller().getId(), sellerId)) throw new CustomException(ShopErrorCode.DIFFERENT_SELLER);
        return shop;
    }
}

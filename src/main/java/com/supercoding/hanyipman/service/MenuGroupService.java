package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.Shop.seller.request.ChangeMenuGroupNameRequest;
import com.supercoding.hanyipman.dto.Shop.seller.request.ChangeMenuGroupRequest;
import com.supercoding.hanyipman.dto.Shop.seller.response.MenuGroupResponse;
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

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuGroupService {

    private final SellerRepository sellerRepository;
    private final ShopRepository shopRepository;
    private final MenuGroupRepository menuGroupRepository;

    @Transactional
    public void createMenuGroup(String menuGroupName, Long shopId) {

        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        Integer sequence = menuGroupRepository.findMaxSequenceByShop(shop);
        shop.addMenuGroup(MenuGroup.from(shop, menuGroupName, sequence+1));

    }

    @Transactional
    public void changeMenuGroupSequence(List<ChangeMenuGroupRequest> requests, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        requests.forEach(request -> shop.menuGroupUpdateSequenceById(request.getMenuGroupId(), request.getMenuGroupSequence()));
    }

    @Transactional
    public List<MenuGroupResponse> findMenuGroupList(Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());

        return shop.getMenuGroups()
                .stream()
                .filter(menuGroup -> !menuGroup.getIsDeleted())
                .sorted(Comparator.comparing(MenuGroup::getSequence))
                .map(MenuGroupResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMenuGroup(Long shopId, Long menuGroupId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        shop.removeMenuGroup(menuGroupId);
    }

    @Transactional
    public void changeMenuGroupName(Long shopId, ChangeMenuGroupNameRequest changeMenuGroupNameRequest) {

        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        shop.menuGroupUpdateNameById(changeMenuGroupNameRequest.getMenuGroupId(), changeMenuGroupNameRequest.getMenuGroupName());
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

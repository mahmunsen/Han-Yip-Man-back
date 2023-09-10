package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.Shop.buyer.request.ViewShopListRequest;
import com.supercoding.hanyipman.dto.Shop.buyer.response.ShopInfoResponse;
import com.supercoding.hanyipman.dto.Shop.buyer.response.ViewShopListResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.Shop;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.shop.ShopCustomRepositoryImpl;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.repository.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuyerShopService {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final ShopRepository shopRepository;
    private final ShopCustomRepositoryImpl shopCustomRepository;

    @Transactional
    public ViewShopListResponse findShopList(ViewShopListRequest viewShopListRequest, CustomUserDetail customUserDetail) {

        Buyer buyer = validBuyerUser(customUserDetail);

        Address address = Optional.ofNullable(buyer.getDefaultAddress()).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_FOUNT_ADDRESS)) ;

        return shopCustomRepository.findShopListWithNextCursor(viewShopListRequest, address.getLatitude(), address.getLongitude());
    }

    public ShopInfoResponse findShopDetail(Long shopId) {
        return ShopInfoResponse.from(validShop(shopId));
    }

    private Buyer validBuyerUser(CustomUserDetail customUserDetail) {
        User validUser = userRepository.findById(customUserDetail.getUserId()).orElseThrow(() -> new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
        return buyerRepository.findBuyerByUserId(validUser.getId()).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }

    private Shop validShop(Long shopId) {
        return shopRepository.findShopByShopId(shopId).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_SHOP));
    }


}

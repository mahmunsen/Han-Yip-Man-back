package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.shop.buyer.request.ViewShopListRequest;
import com.supercoding.hanyipman.dto.shop.buyer.response.ViewShopListResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.shop.ShopCustomRepositoryImpl;
import com.supercoding.hanyipman.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuyerShopService {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final ShopCustomRepositoryImpl shopCustomRepository;

    @Transactional
    public ViewShopListResponse findShopList(ViewShopListRequest viewShopListRequest, CustomUserDetail customUserDetail) {

        Buyer buyer = validBuyerUser(customUserDetail);

        Address address = Optional.ofNullable(buyer.getDefaultAddress()).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_FOUNT_ADDRESS)) ;

        return shopCustomRepository.findShopListWithNextCursor(viewShopListRequest, address.getLatitude(), address.getLongitude());
    }

    private Buyer validBuyerUser(CustomUserDetail customUserDetail) {
        User validUser = userRepository.findById(customUserDetail.getUserId()).orElseThrow(() -> new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
        return buyerRepository.findBuyerByUserId(validUser.getId()).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }

}

package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.myInfo.response.MyInfoAddressResponse;
import com.supercoding.hanyipman.dto.myInfo.response.MyInfoResponse;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class MyInfoService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    private final BuyerRepository buyerRepository;

    @Transactional
    public MyInfoResponse getUserInfoForMyPage(User user) {
        if (Objects.equals(user.getRole(), "SELLER")) {
            throw new CustomException(BuyerErrorCode.INVALID_SELLER);
        }
        Buyer buyer = buyerRepository.findByUser(user);
        if (buyer == null) {
            throw new CustomException(BuyerErrorCode.INVALID_BUYER);
        }

        List<Address> addressList = addressRepository.findAllByBuyer(buyer);
        List<MyInfoAddressResponse> myInfoAddressResponses = addressList.stream().map(MyInfoAddressResponse::toMyAddressResponse).collect(Collectors.toList());

        MyInfoResponse myInfoResponse = MyInfoResponse.toMyInfoResponse(user, buyer, myInfoAddressResponses);
        return myInfoResponse;
    }
}

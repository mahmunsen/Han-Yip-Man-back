package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.address.request.AddressRegisterRequest;
import com.supercoding.hanyipman.dto.address.response.AddressListResponse;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.AddressErrorCode;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final BuyerRepository buyerRepository;

    //TODO 주소등록
    @Transactional
    public String registerAddress(User user, AddressRegisterRequest request) {
        Integer limitAddress = 3; // 개인당 가질 수 있는 주소 갯수
        //Todo 주소 3개초과 등록 못함, 주소 빈값 검사
        requestNullCheck(request);
        Buyer buyerId = buyerRepository.findByUser(user);
        if (buyerId.getAddresses() == null) throw new CustomException(BuyerErrorCode.INVALID_BUYER);
//        if (addressRepository.existsAllByAddress(request.getAddress()) >= 1)
//            throw new CustomException(AddressErrorCode.DUPLICATE_ADDRESS);
        Integer allByUserCountId = addressRepository.findAllByUserCountId(buyerId.getId());
        if (allByUserCountId >= limitAddress) throw new CustomException(AddressErrorCode.ADDRESS_DATA_EXCEED_LIMIT);

        Buyer buyer = buyerRepository.findById(buyerId.getId()).orElseThrow(() -> new CustomException(BuyerErrorCode.INVALID_BUYER));
        Address address = Address.toAddAddress(buyer, request);

        Address save = addressRepository.save(address);
        setDefaultAddress(user, address.getId());
        return save.getAddress();
    }

    // Todo 주소 목록 조회
    @TimeTrace//9월7일 Total time = 0.083849917s
    @Transactional
    public List<AddressListResponse> getAddressList(User user) {
//        buyerRepository.findByUser(user).getId();
//        addressRepository.findAllByGetAddressList(user);
        Buyer byUser = buyerRepository.findByUser(user);

        addressRepository.findAllByBuyer(byUser);

        return addressRepository.findAllByBuyer(byUser).stream().map(AddressListResponse::toaAddressListResponse).collect(Collectors.toList());
    }

    // Todo 주소 수정
    @Transactional
    public Long patchAddress(User user, Long addressId, AddressRegisterRequest request) {
        requestNullCheck(request);
        Optional<Address> address = addressRepository.findByBuyerAndId(user.getBuyer(), addressId);
        if (address.isEmpty()) throw new CustomException(AddressErrorCode.UNCHANGEABLE_ADDRESS);
        patchSetAdd(address.get(), request);
        return address.get().getId();
    }


    // Todo 주소 삭제

    @Transactional
    public String sellerDeleteAddress(User user, Long addressId) {
        Optional<Address> address = addressRepository.findByBuyerAndId(user.getBuyer(), addressId);
        if (address.isEmpty()) throw new CustomException(AddressErrorCode.ADDRESS_NOT_FOUND);
        addressRepository.deleteAddressByAddress(user.getBuyer(), address.get().getId());
        return address.get().getAddress();

    }

    public void patchSetAdd(Address address, AddressRegisterRequest request) {
        address.setAddress(request.getAddress());
        address.setDetailAddress(request.getAddressDetail());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
    }

    public void requestNullCheck(AddressRegisterRequest request) {
        if (request.getAddress() == null || "".equals(request.getAddress()) || request.getAddressDetail() == null || "".equals(request.getAddressDetail()) || request.getLatitude() == null || request.getLongitude() == null)
            throw new CustomException(AddressErrorCode.EMPTY_ADDRESS_DATA);
    }

    @Transactional
    public void setDefaultAddress(User user, Long defaultAddressId) {
        List<Address> addressList = addressRepository.findAllByBuyer(user.getBuyer());

        if (addressList.stream().noneMatch(address -> address.getId().equals(defaultAddressId)))
            throw new CustomException(AddressErrorCode.MY_ADDRESS_ONLY);

        addressList.stream().map(address -> {
            if (address.getId().equals(defaultAddressId)) {
                address.setIsDefault(true);
            } else {
                address.setIsDefault(false);
            }
            return address;
        }).collect(Collectors.toList());
    }
}









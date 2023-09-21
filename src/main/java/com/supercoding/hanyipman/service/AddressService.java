package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.address.request.AddressRegisterRequest;
import com.supercoding.hanyipman.dto.address.response.AddressListResponse;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.AddressErrorCode;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    // 주소등록
    @TimeTrace
    @Transactional
    public String registerAddress(Buyer buyer, AddressRegisterRequest request) {
        mapIdNullCheck(request);
        validateUniqueAddressId(buyer, request);
        Address address = Address.toAddAddress(buyer, request);
        addressRepository.save(address);
        setDefaultAddress(buyer, address.getId());
        return address.getAddress();
    }

    // 주소 목록 조회
    @TimeTrace
    public List<AddressListResponse> getAddressList(Buyer buyer) {
        return addressRepository.findAllByBuyer(buyer).stream().map(AddressListResponse::toaAddressListResponse).collect(Collectors.toList());
    }

    // 주소 수정
    @TimeTrace
    @Transactional
    public Long patchAddress(Buyer buyer, Long addressId, AddressRegisterRequest request) {
        mapIdNullCheck(request);
        Address address = addressRepository.findByBuyerAndId(buyer, addressId).orElseThrow(() -> new CustomException(AddressErrorCode.UNCHANGEABLE_ADDRESS));
        patchSetAdd(address, request);
        return address.getId();
    }

    //  주소 삭제
    @TimeTrace
    @Transactional
    public String sellerDeleteAddress(Buyer buyer, Long addressId) {
        if (addressRepository.countAddressByBuyer(buyer) <= 1)
            throw new CustomException(AddressErrorCode.ADDRESS_DATA_EXCEED_LIMIT);
        Address address = addressRepository.findByBuyerAndId(buyer, addressId).orElseThrow(() -> new CustomException(AddressErrorCode.ADDRESS_NOT_FOUND));
        // 삭제하려는 주소가 기본 주소로 등록되어 있을 때 최근 주소를 기본주소로 설정
        if (address.getIsDefault()) {
            List<Address> addresses = addressRepository.findAllByBuyerAndIsDefaultFalseOrderByIdDesc(buyer);
            setDefaultAddress(buyer, addresses.get(0).getId());
        }

        addressRepository.deleteAddressByAddress(buyer, address.getId());
        return address.getAddress();
    }

    // 기본 주소 등록
    @TimeTrace
    @Transactional
    public void setDefaultAddress(Buyer buyer, Long defaultAddressId) {
        List<Address> addressList = addressRepository.findAllByBuyer(buyer);
        // 나의 주소가 맞는지 확인
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

    // 주소 수정 set
    public void patchSetAdd(Address address, AddressRegisterRequest request) {
        address.setAddress(request.getAddress());
        address.setDetailAddress(request.getAddressDetail());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setMapId(request.getMapId());
        address.setRoadAddress(request.getRoadAddress());
    }

    // mapId null 체크
    public void mapIdNullCheck(AddressRegisterRequest request) {
        if (request.getMapId() == null) throw new CustomException(AddressErrorCode.EMPTY_ADDRESS_DATA);
    }

    // 카카오 mapId 중복 예외처리
    private void validateUniqueAddressId(Buyer buyer, AddressRegisterRequest request) {
        if (addressRepository.existsAddressByMapIdAndBuyer(request.getMapId(), buyer))
            throw new CustomException(AddressErrorCode.DUPLICATE_ADDRESS);
    }

    public Boolean checkDuplicationAddress(String mapId) {
        Buyer buyer = JwtToken.user().validBuyer();
        return addressRepository.existsAddressByMapIdAndBuyer(mapId, buyer);
    }
}









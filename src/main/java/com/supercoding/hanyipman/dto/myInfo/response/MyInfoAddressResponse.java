package com.supercoding.hanyipman.dto.myInfo.response;

import com.supercoding.hanyipman.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyInfoAddressResponse {
    private Long addressNumber;
    private String address;
    private String detailAddress;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;

    public static MyInfoAddressResponse toMyAddressResponse(Address addressList) {
        return MyInfoAddressResponse.builder()
                .addressNumber(addressList.getId())
                .address(addressList.getAddress())
                .detailAddress(addressList.getDetailAddress())
                .latitude(addressList.getLatitude())
                .longitude(addressList.getLongitude())
                .isDefault(addressList.getIsDefault())
                .build();
    }
}

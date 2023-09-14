package com.supercoding.hanyipman.dto.address.response;

import com.supercoding.hanyipman.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressListResponse {

    private Long addressId;
    private String address;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
    private String roadAddress;
    private String mapId;

    public static AddressListResponse toaAddressListResponse(Address address) {
        return AddressListResponse.builder()
                .addressId(address.getId())
                .address(address.getAddress())
                .addressDetail(address.getDetailAddress())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .isDefault(address.getIsDefault())
                .roadAddress(address.getRoadAddress())
                .mapId(address.getMapId())
                .build();
    }
}

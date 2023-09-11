package com.supercoding.hanyipman.dto.Shop.buyer.response;

import com.supercoding.hanyipman.entity.Address;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link com.supercoding.hanyipman.entity.Address}
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopAddressResponse implements Serializable {
    private String address;
    private String detailAddress;
    private Double latitude;
    private Double longitude;

    public static ShopAddressResponse from(Address address) {
        return ShopAddressResponse.builder()
                .address(address.getAddress())
                .detailAddress(address.getDetailAddress())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }
}
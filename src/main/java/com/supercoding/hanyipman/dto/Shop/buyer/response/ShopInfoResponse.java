package com.supercoding.hanyipman.dto.Shop.buyer.response;

import com.supercoding.hanyipman.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopInfoResponse {

    private String bannerUrl;
    private String shopName;
    private String description;
    private ShopAddressResponse shopAddressResponse;

    public static ShopInfoResponse from(Shop shop) {
        return ShopInfoResponse.builder()
                .bannerUrl(shop.getBanner())
                .shopName(shop.getName())
                .description(shop.getDescription())
                .shopAddressResponse(ShopAddressResponse.from(shop.getAddress()))
                .build();
    }

}

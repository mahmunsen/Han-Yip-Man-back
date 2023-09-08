package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopDetailResponse {

    private String shopName;
    private String thumbnailUrl;
    private String bannerUrl;
    private String shopPhone;
    private String categoryName;
    private String address;
    private String detailAddress;
    private String businessNumber;
    private Integer minOrderPrice;
    private String shopDescription;

    public static ShopDetailResponse from(Shop shop) {
        return ShopDetailResponse.builder()
                .shopName(shop.getName())
                .thumbnailUrl(shop.getThumbnail())
                .bannerUrl(shop.getBanner())
                .shopPhone(shop.getPhoneNum())
                .categoryName(shop.getCategory().getName())
                .address(shop.getAddress().getAddress())
                .detailAddress(shop.getAddress().getDetailAddress())
                .businessNumber(shop.getBusinessNumber())
                .minOrderPrice(shop.getMinOrderPrice())
                .shopDescription(shop.getDescription())
                .build();
    }

}

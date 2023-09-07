package com.supercoding.hanyipman.dto.shop.seller.response;

import com.supercoding.hanyipman.entity.Shop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopManagementListResponse {

    private Long shopId;
    private String name;

    public static ShopManagementListResponse from(Shop shop) {
        return ShopManagementListResponse.builder()
                .shopId(shop.getId())
                .name(shop.getName())
                .build();
    }

}

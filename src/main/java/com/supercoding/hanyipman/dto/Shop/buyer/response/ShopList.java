package com.supercoding.hanyipman.dto.Shop.buyer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopList {

    private Long shopId;
    private String shopName;
    private String thumbnailUrl;
    private String shopDescription;
    private Integer minOrderPrice;
    private Double avgRating;
    private Long reviewCount;
    private Integer deliveryTime;
    private Integer deliveryPrice;
    private Integer distance;

}

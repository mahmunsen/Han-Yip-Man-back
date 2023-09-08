package com.supercoding.hanyipman.dto.Shop.buyer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewShopListResponse {

    private List<ShopList> shopLists;
    private Long nextCursor;

}

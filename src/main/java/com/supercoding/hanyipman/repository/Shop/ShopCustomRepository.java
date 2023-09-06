package com.supercoding.hanyipman.repository.Shop;

import com.supercoding.hanyipman.dto.shop.buyer.request.ViewShopListRequest;
import com.supercoding.hanyipman.dto.shop.buyer.response.ViewShopListResponse;

public interface ShopCustomRepository {

    ViewShopListResponse findShopListWithNextCursor(ViewShopListRequest viewShopListRequest , Double latitude, Double longitude);

}

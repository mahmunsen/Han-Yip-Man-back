package com.supercoding.hanyipman.repository.shop;

import com.supercoding.hanyipman.dto.Shop.buyer.request.ViewShopListRequest;
import com.supercoding.hanyipman.dto.Shop.buyer.response.ViewShopListResponse;

public interface ShopCustomRepository {

    ViewShopListResponse findShopListWithNextCursor(ViewShopListRequest viewShopListRequest , Double latitude, Double longitude);

}

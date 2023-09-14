package com.supercoding.hanyipman.dto.Shop.seller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사장님 가게 주문 조회")
public class ShopOrderCategorizedResponse {
    private List<ShopOrderResponse> paid;
    private List<ShopOrderResponse> takeover;
    private List<ShopOrderResponse> cooking;
    private List<ShopOrderResponse> delivery;
}

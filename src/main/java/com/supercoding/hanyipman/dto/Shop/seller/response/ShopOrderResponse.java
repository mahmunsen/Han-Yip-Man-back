package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사장님 가게 주문 조회")
public class ShopOrderResponse {
    private Long orderId;
    private String orderStatus;
    private Integer totalAmount;
    private String address;
    private Instant orderedTime;
    private List<ShopOrdersMenuResponse> menuResponses;
    private Integer orderSequence;





    public static ShopOrderResponse from(Order order) {
        return ShopOrderResponse.builder()
                .orderStatus(order.getOrderStatus().name())
                .orderId(order.getId())
                .totalAmount(order.getTotalPrice())
                .address(order.getAddress().getAddress()+" "+order.getAddress().getDetailAddress())
                .orderedTime(order.getUpdatedAt())
                .menuResponses(order.getCarts().stream().map(cart -> ShopOrdersMenuResponse.from(cart)).collect(Collectors.toList()))
                .orderSequence(order.getOrderSequence())
                .build();

    }

}

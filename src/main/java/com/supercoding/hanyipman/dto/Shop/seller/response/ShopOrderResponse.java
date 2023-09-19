package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.Order;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value="주문 식별값 필드", dataType = "Long")
    private Long orderId;
    @ApiModelProperty(value="주문 상태 필드", dataType = "String")
    private String orderStatus;
    @ApiModelProperty(value="총 가격 필드", dataType = "Integer")
    private Integer totalAmount;
    @ApiModelProperty(value = "주소", dataType = "String")
    private String address;
    @ApiModelProperty(value="주문시간", dataType = "Instant")
    private Instant orderedTime;
    @ApiModelProperty(value="주문 메뉴 필드", dataType = "List<OrderMenuResponse>")
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

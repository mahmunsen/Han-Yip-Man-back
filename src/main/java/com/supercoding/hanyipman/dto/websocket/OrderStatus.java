package com.supercoding.hanyipman.dto.websocket;

import lombok.Data;

@Data
public class OrderStatus {

    private String room;
    private OrderType orderType;
    private String message;

    public OrderStatus() {

    }

    public OrderStatus(OrderType orderType, String message) {
        this.orderType = orderType;
        this.message = message;
    }
}

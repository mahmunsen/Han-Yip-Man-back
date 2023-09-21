package com.supercoding.hanyipman.dto.delivery.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDeliveryAddress {
    private Long startAddrId;
    private Long endAddrId;
}

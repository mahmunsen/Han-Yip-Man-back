package com.supercoding.hanyipman.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventName {
    NOTICE_ORDER("NoticeOrder"),
    NOTICE_ORDER_STATUS("NoticeOrderStatus"),
    DRON_LOCATION("DronLocation"),
    SUBSCRIBE("SubScribe");

    private final String eventName;
}

package com.supercoding.hanyipman.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventName {
    NOTICE_ORDER_BUYER("NoticeOrderBuyer"),
    NOTICE_ORDER_SELLER("NoticeOrderSeller"),
    NOTICE_DRON_LOCATION("NoticeDronLocation"),
    SUBSCRIBE("SubScribe");

    private final String eventName;
}

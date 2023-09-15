package com.supercoding.hanyipman.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventName {
    NOTICE_ORDER("NoticeOrder"),
    CUR_DRON_POSITION("getCurDronPosition"),
    SUBSCRIBE("SubScribe");

    private final String eventName;
}

package com.supercoding.hanyipman.enums;

import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;

public enum ShopSortType {
    DISTANCE, //거리 가까운 순
    AVG_RATING, //별점 높은 순
    COUNT_REVIEW,   // 리뷰 많은 수
    CREATED_AT;  // 최신 순

    public static ShopSortType fromString(String value) {
        try {
            if (value == null || "".equals(value)) value = "CREATED_AT";
            return ShopSortType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ShopErrorCode.NOT_FOUND_SORT_TYPE);
        }
    }
}

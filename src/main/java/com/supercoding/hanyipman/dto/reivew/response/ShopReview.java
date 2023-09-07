package com.supercoding.hanyipman.dto.reivew.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class ShopReview {
    private Long userId;
    private String nickName;
    private String reviewContent;
    private Integer reviewScore;
    private Instant createdAt;
    private String reviewImageUrl;
}

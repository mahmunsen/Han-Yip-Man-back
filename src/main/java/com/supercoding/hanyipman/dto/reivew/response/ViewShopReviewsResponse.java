package com.supercoding.hanyipman.dto.reivew.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ViewShopReviewsResponse {
    private List<ShopReview> shopReviewsList;
    private Instant cursor;

}

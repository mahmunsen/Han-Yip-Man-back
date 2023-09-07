package com.supercoding.hanyipman.dto.reivew.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상점 리뷰 조회 DTO")
public class ViewShopReviewsRequest {
    private Integer reviewScore;
    private Integer size;
    private Instant cursor;
}

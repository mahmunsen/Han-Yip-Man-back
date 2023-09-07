package com.supercoding.hanyipman.dto.reivew.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "상점 리뷰 조회 DTO")
public class ViewShopReviewsRequest {
    private Integer reviewScore;
    private Integer size;
    private Instant cursor;
}

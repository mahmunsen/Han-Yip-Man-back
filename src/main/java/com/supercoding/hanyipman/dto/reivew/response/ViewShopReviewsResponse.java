package com.supercoding.hanyipman.dto.reivew.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "상점 리뷰 조회 응답 DTO")
public class ViewShopReviewsResponse {
    @ApiModelProperty(value="상점 리뷰들 필드", dataType = "List")
    private List<ShopReview> shopReviewsList;
    @ApiModelProperty(value="이후 요청할 커서(등록일자 커서) 필드", dataType = "Instant")
    private Instant cursor;

}

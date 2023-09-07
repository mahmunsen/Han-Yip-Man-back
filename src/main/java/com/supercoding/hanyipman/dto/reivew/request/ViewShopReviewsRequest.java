package com.supercoding.hanyipman.dto.reivew.request;

import io.swagger.annotations.ApiModelProperty;
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
@Schema(description = "상점 리뷰 조회 요청 DTO")
public class ViewShopReviewsRequest {
    @ApiModelProperty(value="리뷰 별점 필드", dataType = "Integer")
    private Integer reviewScore;
    @ApiModelProperty(value="리뷰 조회 요청 사이즈 필드", dataType = "Integer")
    private Integer size;
    @ApiModelProperty(value="리뷰 조회 요청 커서 필드(등록 일자 커서)", dataType = "Instant")
    private Instant cursor;
}

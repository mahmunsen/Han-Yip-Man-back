package com.supercoding.hanyipman.dto.reivew.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
@Schema(description = "리뷰 응답 DTO")
public class ShopReview {
    @ApiModelProperty(value="리뷰 작성 유저 식별값 필드", dataType = "Long")
    private Long userId;
    @ApiModelProperty(value="리뷰 작성 유저 닉네임 필드", dataType = "String")
    private String nickName;
    @ApiModelProperty(value="리뷰 내용 필드", dataType = "String")
    private String reviewContent;
    @ApiModelProperty(value="리뷰 별점 필드", dataType = "Integer")
    private Integer reviewScore;
    @ApiModelProperty(value="리뷰 작성 일시 필드", dataType = "Instant")
    private Instant createdAt;
    @ApiModelProperty(value="리뷰 이미지 URL 필드", dataType = "String")
    private String reviewImageUrl;
}

package com.supercoding.hanyipman.dto.reivew.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "고객 리뷰 등록 요청 DTO")
public class RegisterReviewRequest {
    @ApiModelProperty(value="상점 식별값 필드", dataType = "Long")
    private Long shopId;
    @ApiModelProperty(value="리뷰 내용 필드", dataType = "String")
    private String reviewContent;
    @ApiModelProperty(value="리뷰 별점 필드", dataType = "Integer")
    private Integer reviewScore;
    @ApiModelProperty(value="리뷰 이미지 필드", dataType = "MultipartFile")
    private MultipartFile reviewImage;
}

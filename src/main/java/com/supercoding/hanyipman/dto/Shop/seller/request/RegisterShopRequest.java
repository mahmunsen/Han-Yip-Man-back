package com.supercoding.hanyipman.dto.Shop.seller.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "가게 등록 DTO")
public class RegisterShopRequest {

    @NotBlank(message = "가게 이름을 입력해주세요.")
    @ApiModelProperty(value = "음식점 이름 입력 필드", dataType = "String")
    private String shopName;
    @NotBlank(message = "가게 전화번호를 입력해주세요.")
    @ApiModelProperty(value = "음식점 전화번호 입력 필드", dataType = "String")
    private String shopPhone;

    @NotBlank(message = "카테고리 번호를 입력해주세요.")
    @ApiModelProperty(value = "카테고리 번호 입력 필드", dataType = "String")
    private Long categoryId;

//    private ShopAddressRequest shopAddressRequest;
    @NotBlank(message = "가게 사업자 등록 번호를 입력해주세요.")
    @ApiModelProperty(value = "가게 사업자 등록 번호 입력 필드", dataType = "String")
    private String businessNumber;
    @NotBlank(message = "최소 주문금액을 입력해주세요.")
    @ApiModelProperty(value = "최소 주문금액 입력 필드", dataType = "Integer")
    private Integer minOrderPrice;
    @NotBlank(message = "가게 소개를 입력해주세요.")
    @ApiModelProperty(value = "가게 소개 입력 필드", dataType = "String")
    private String showDescription;

}

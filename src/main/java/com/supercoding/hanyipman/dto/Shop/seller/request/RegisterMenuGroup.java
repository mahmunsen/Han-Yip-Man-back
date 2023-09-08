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
@Schema(description = "대분류 등록 DTO")
public class RegisterMenuGroup {
    @NotBlank(message = "대분류 이름을 입력해주세요.")
    @ApiModelProperty(value = "대분류 이름 입력 필드", dataType = "String")
    private String menuGroupName;
}

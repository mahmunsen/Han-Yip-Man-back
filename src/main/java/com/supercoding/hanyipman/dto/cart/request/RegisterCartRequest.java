package com.supercoding.hanyipman.dto.cart.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCartRequest {

    @ApiModelProperty(value = "음식점 ID", example = "1")
    private Long shopId;

    @ApiModelProperty(value = "메뉴 ID", example = "1")
    private Long menuId;

    @ApiModelProperty(value = "옵션 IDS", example = "[ 1, 2]")
    private ArrayList<Long> options;

    @ApiModelProperty(value = "메뉴 갯수", example = "3")
    private Long amount;
}

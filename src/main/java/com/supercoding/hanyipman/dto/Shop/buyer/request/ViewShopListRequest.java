package com.supercoding.hanyipman.dto.Shop.buyer.request;

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
@Schema(description = "구매자 가게 조회 DTO")
public class ViewShopListRequest {

    private Long categoryId;
    @NotBlank
    private Integer size;
    private String searchKeyword;
    private String sortType;
    private Long cursorId;
    private String cursorValue;
}

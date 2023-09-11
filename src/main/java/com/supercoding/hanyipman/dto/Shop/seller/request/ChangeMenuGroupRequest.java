package com.supercoding.hanyipman.dto.Shop.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "대분류 순서 변경 DTO")
public class ChangeMenuGroupRequest {

    private Long menuGroupId;
    private Integer menuGroupSequence;

}

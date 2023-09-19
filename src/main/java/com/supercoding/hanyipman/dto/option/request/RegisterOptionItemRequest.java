package com.supercoding.hanyipman.dto.option.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOptionItemRequest {

    private String name;
    private Integer price;

}

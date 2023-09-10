package com.supercoding.hanyipman.dto.Shop.seller.response;

import com.supercoding.hanyipman.entity.Option;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponse implements Serializable {
    private Long optionId;
    private String optionName;
    private Integer maxSelected;
    private Boolean isMultiple;
    private List<OptionItemDto> optionItems;

    public static OptionResponse from(Option option) {
        return OptionResponse.builder()
                .optionId(option.getId())
                .optionName(option.getName())
                .maxSelected(option.getMaxSelected())
                .isMultiple(option.getIsMultiple())
                .optionItems(option.getOptionItems().stream().map(OptionItemDto::from).collect(Collectors.toList()))
                .build();
    }
}
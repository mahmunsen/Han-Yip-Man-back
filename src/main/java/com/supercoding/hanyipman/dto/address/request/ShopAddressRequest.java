package com.supercoding.hanyipman.dto.address.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopAddressRequest {

    @NotBlank(message = "가게 주소를 입력해주세요.")
    @ApiModelProperty(value = "가게 주소 입력 필드", dataType = "String", name = "address")
    @JsonProperty(value="address")
    private String address;

    @ApiModelProperty(value = "가게 상세 주소 입력 필드", dataType = "String", name = "addressDetail")
    @JsonProperty(value="address_detail")
    private String addressDetail;
    @NotBlank(message = "경도를 입력해주세요.")
    @ApiModelProperty(value = "경도 입력 필드", dataType = "double", name= "longitude")
    @JsonProperty(value="longitude")
    private double longitude;
    @NotBlank(message = "위도를 입력해주세요.")
    @ApiModelProperty(value = "위도 입력 필드", dataType = "double", name = "latitude")
    @JsonProperty(value="latitude")
    private double latitude;

}

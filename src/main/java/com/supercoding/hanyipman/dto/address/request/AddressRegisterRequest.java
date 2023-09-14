package com.supercoding.hanyipman.dto.address.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.supercoding.hanyipman.entity.Address;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Schema(description = "주소 및 좌표")
public class AddressRegisterRequest {
    @ApiModelProperty(value = "가게 주소 입력 필드", dataType = "String", name = "address")
    @JsonProperty(value = "address")
    private String address;

    @ApiModelProperty(value = "가게 주소 상세 입력 필드", dataType = "String", name = "addressDetail")
    @JsonProperty(value = "address_detail")
    private String addressDetail;

    @ApiModelProperty(value = "가게 위도 입력 필드", dataType = "Double", name = "latitude")
    @JsonProperty(value = "latitude")
    private Double latitude;

    @ApiModelProperty(value = "가게 경도 입력 필드", dataType = "Double", name = "longitude")
    @JsonProperty(value = "longitude")
    private Double longitude;

    @ApiModelProperty(value = "가게 도로명 주소 입력 필드", dataType = "String", name = "roadAddress")
    @JsonProperty(value = "road_address")
    private String roadAddress;

    @ApiModelProperty(value = "가게 맵Id 입력 필드", dataType = "String", name = "mapId")
    @JsonProperty(value = "map_id")
    private String mapId;
}

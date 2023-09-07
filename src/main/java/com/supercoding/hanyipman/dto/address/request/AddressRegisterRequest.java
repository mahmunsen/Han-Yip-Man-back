package com.supercoding.hanyipman.dto.address.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.supercoding.hanyipman.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Schema(description = "주소 및 좌표")
public class AddressRegisterRequest {

    //Todo: 스웨거 가독성 좋게 설정 더하기
    @JsonProperty(value = "address")
    private String address;
    @JsonProperty(value = "address_detail")
    private String addressDetail;
    @JsonProperty(value = "latitude")
    private Double latitude;
    @JsonProperty(value = "longitude")
    private Double longitude;
}

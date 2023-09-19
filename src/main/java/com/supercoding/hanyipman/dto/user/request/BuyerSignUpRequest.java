package com.supercoding.hanyipman.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerSignUpRequest implements SignUpRequest {
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "password")
    private String password;
    @JsonProperty(value = "passwordCheck")
    private String passwordCheck;
    @JsonProperty(value = "phoneNumber")
    private String phoneNumber;
    @JsonProperty(value = "nickName")
    private String nickName;
    @JsonProperty(value = "address")
    private String address;
    @JsonProperty(value = "addressDetail")
    private String addressDetail;
    @JsonProperty(value = "latitude")
    private Double latitude;
    @JsonProperty(value = "longitude")
    private Double longitude;
    @JsonProperty(value = "road_address")
    private String roadAddress;
    @JsonProperty(value = "map_id")
    private String mapId;
}

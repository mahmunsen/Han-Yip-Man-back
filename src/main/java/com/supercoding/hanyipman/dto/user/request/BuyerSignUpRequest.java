package com.supercoding.hanyipman.dto.user.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BuyerSignUpRequest {
    private String email;
    private String password;
    private String passwordCheck;
    private String phoneNumber;
    private String nickName;
    private String profileImageFile;
    private String address;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
}

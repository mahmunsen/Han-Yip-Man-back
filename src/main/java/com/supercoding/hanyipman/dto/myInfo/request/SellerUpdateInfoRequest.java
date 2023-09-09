package com.supercoding.hanyipman.dto.myInfo.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerUpdateInfoRequest {
    private Long userNumber;
    private String phoneNumber;
    private String nickName;
    private String password;
    private String passwordCheck;
    private String businessNumber;
}

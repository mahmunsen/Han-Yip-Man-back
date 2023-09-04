package com.supercoding.hanyipman.dto.user.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SellerSignUpRequest {
    private String email;
    private String password;
    private String passwordCheck;
    private String phoneNumber;
    private String nickName;
    private String businessNumber;



}

package com.supercoding.hanyipman.dto.myInfo.response;

import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyInfoResponse {
    private Long userNumber;
    private Long buyNumber;
    private String email;
    private String phoneNumber;
    private String nickName;
    private String profileImageUrl;
    private String role;
    private List<MyInfoAddressResponse> addressList;


    public static MyInfoResponse toMyInfoResponse(User user, Buyer buyer, List<MyInfoAddressResponse> addressList) {
        return MyInfoResponse.builder()
                .userNumber(user.getId())
                .buyNumber(buyer.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNum())
                .nickName(user.getNickname())
                .profileImageUrl(buyer.getProfile())
                .role(user.getRole())
                .addressList(addressList)
                .build();
    }


}

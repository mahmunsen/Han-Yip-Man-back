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
    private String email;
    private String phoneNumber;
    private String nickName;
    private String profileImageUrl;
    private List<Address> address;

    public static MyInfoResponse toMyInfoResponse(User user, Buyer buyer, List<Address> addressList) {
        return MyInfoResponse.builder()
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNum())
                .nickName(user.getNickname())
                .profileImageUrl(buyer.getProfile())
                .address(addressList)
                .build();

    }
}

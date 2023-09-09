package com.supercoding.hanyipman.dto.myInfo.response;

import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.Seller;
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
    private Long sellerNumber;
    private String email;
    private String phoneNumber;
    private String nickName;
    private String businessNumber;
    private String role;
    private String profileImageUrl;
    private List<MyInfoAddressResponse> addressList;


    public static MyInfoResponse toBuyerInfoResponse(User user, Buyer buyer, List<MyInfoAddressResponse> addressList) {
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

    public static MyInfoResponse toSellerInfoResponse(User user, Seller seller) {
        return MyInfoResponse.builder()
                .userNumber(user.getId())
                .sellerNumber(user.getSeller().getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNum())
                .nickName(user.getNickname())
                .businessNumber(seller.getBusinessNumber())
                .role(user.getRole())
                .build();
    }


}

package com.supercoding.hanyipman.dto.user.response;

import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String accessToken;
    private String role;
    private String profileUrl;


    public static LoginResponse toLoginBuyerResponse(User user, String jwtToken, Buyer buyerUser) {
        return LoginResponse.builder()
                .accessToken(jwtToken)
                .role(user.getRole())
                .profileUrl(buyerUser.getProfile())
                .build();
    }

    public static LoginResponse toLoginSellerResponse(User user, String jwtToken, Seller buyerUser) {
        return LoginResponse.builder()
                .accessToken(jwtToken)
                .role(user.getRole())
                .build();
    }
}

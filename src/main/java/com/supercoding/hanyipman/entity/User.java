package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.myInfo.request.SellerUpdateInfoRequest;
import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import com.supercoding.hanyipman.dto.user.response.KakaoUserInfoResponse;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.error.domain.SellerErrorCode;
import com.supercoding.hanyipman.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.Optional;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Lob
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    @Column(name = "phone_num", nullable = false, length = 20)
    private String phoneNum;

    @Column(name = "role", nullable = false, length = 8)
    private String role;

    @Column(name = "auth_provider", length = 9)
    private String authProvider;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Buyer buyer;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Seller seller;

    public Seller validSeller() {
        return Optional.ofNullable(this.seller).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
    }

    public Buyer validBuyer() {
        return Optional.ofNullable(this.buyer).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }

    public static User toSellerSignup(SellerSignUpRequest request, String password) {
        return User.builder()
                .email(request.getEmail())
                .password(password)
                .nickname(request.getNickName())
                .phoneNum(request.getPhoneNumber())
                .role(UserRole.SELLER.name())
                .authProvider(null)
                .build();
    }

    public static User toBuyerSignup(BuyerSignUpRequest request, String password) {
        return User.builder()
                .email(request.getEmail())
                .password(password)
                .nickname(request.getNickName())
                .phoneNum(request.getPhoneNumber())
                .role(UserRole.BUYER.name())
                .authProvider(null)
                .build();
    }

    public static User sellerUpdateMyInfo(User user, Seller seller, SellerUpdateInfoRequest request) {
        return User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickName())
                .phoneNum(request.getPhoneNumber())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .isDeleted(user.getIsDeleted())
                .seller(seller)
                .build();
    }

    public static User kakaoBuyerSignup(KakaoUserInfoResponse response, String password) {
        return User.builder()
                .email(response.getKakao_account().getEmail())
                .nickname(response.getProperties().getNickname())
                .password(password)
                .phoneNum("010-0000-0000")
                .role(UserRole.BUYER.name())
                .authProvider(null)
                .build();
    }
}
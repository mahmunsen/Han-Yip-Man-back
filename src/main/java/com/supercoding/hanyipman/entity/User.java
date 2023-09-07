package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import com.supercoding.hanyipman.security.UserRole;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
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

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL, orphanRemoval = true)
    private Buyer buyer;

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
}
package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buyer")
public class Buyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "profile")
    private String profile;

    public static Buyer tobuyer(User user, BuyerSignUpRequest request) {
        return Buyer.builder()
                .user(user)
                .profile(request.getProfileImageFile())
                .build();
    }
}
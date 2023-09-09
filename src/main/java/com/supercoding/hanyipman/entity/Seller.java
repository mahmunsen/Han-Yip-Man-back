package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.myInfo.request.SellerUpdateInfoRequest;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.mapping.ToOne;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@DynamicUpdate
@NoArgsConstructor
@Table(name = "seller")
public class Seller {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "business_number", nullable = false, length = 12)
    private String businessNumber;

    public static Seller toSeller(SellerSignUpRequest request, User user) {
        return Seller.builder()
                .user(user)
                .businessNumber(request.getBusinessNumber())
                .build();
    }

    public static Seller updateBusinessNum(Seller seller, SellerUpdateInfoRequest request) {
        return Seller.builder()
                .id(seller.getId())
                .user(seller.getUser())
                .businessNumber(request.getBusinessNumber())
                .build();
    }


}
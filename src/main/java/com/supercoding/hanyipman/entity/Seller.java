package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seller")
public class Seller {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
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

}
package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.address.request.ShopAddressRequest;
import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@DynamicInsert
@NoArgsConstructor
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private Buyer buyer;

    @OneToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "is_default")
    private Boolean isDefault;


    public static Address toBuyerAddress(BuyerSignUpRequest request, Buyer buyer) {
        return Address.builder()
                .buyer(buyer)
                .address(request.getAddress())
                .detailAddress(request.getAddressDetail())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDefault(true)
                .build();
    }

    public static Address from(ShopAddressRequest shopAddressRequest) {
        return Address.builder()
                .address(shopAddressRequest.getAddress())
                .detailAddress(shopAddressRequest.getAddressDetail())
                .latitude(shopAddressRequest.getLatitude())
                .longitude(shopAddressRequest.getLongitude())
                .isDefault(true)
                .build();
    }
}
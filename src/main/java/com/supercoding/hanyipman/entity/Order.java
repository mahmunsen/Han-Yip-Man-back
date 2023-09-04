package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "buyer_coupon_id", nullable = false)
    private Long buyerCouponId;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

}
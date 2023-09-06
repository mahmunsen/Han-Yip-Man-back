package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
@ToString
@Getter
@Setter
@Entity
@Table(name = "`order_test`")
public class OrderTest {
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

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;
}

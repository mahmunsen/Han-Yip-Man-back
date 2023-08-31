package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "buyer_coupon_id", nullable = false)
    private Long buyerCouponId;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

}
package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "buyer_coupon")
public class BuyerCoupon {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

}
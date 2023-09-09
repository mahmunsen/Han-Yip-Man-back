package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;

@ToString
@Getter
@Setter
@Entity
@Table(name = "order_test")
public class OrderTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_uid", nullable = false)
    private String orderUid;

    @Column(name = "buyer_coupon_id", nullable = false)
    private Long buyerCouponId;

    @Column(name = "status", nullable = false, length = 10)
    private String status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyerId;

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

}

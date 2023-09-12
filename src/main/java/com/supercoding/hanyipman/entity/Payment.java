package com.supercoding.hanyipman.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "imp_uid", nullable = false)
    private String impUid;

    @Column(name = "merchant_uid", nullable = false)
    private String merchantUid;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(name = "payment_date", nullable = false)
    private Instant paymentDate;

    @Column(name = "cancellation_date")
    private Instant cancellationDate;

    @JoinColumn(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    public static Payment importFrom(Order order, String merchantUid, Long sellerId) {
        return Payment.builder()
                .order(order)
                .impUid(null)
                .merchantUid(merchantUid)
                .buyerId(order.getBuyer().getId())
                .totalAmount(order.getTotalPrice())
                .paymentMethod("html5_inicis")
                .paymentStatus("ready")
                .paymentDate(Instant.now())
                .sellerId(sellerId)
                .build();
    }
    public static Payment kakaoFrom(Order order, String merchantUid, String tid, Long sellerId) {
        return Payment.builder()
                .order(order)
                .impUid(tid)
                .merchantUid(merchantUid)
                .buyerId(order.getBuyer().getId())
                .totalAmount(order.getTotalPrice())
                .paymentMethod("KakaoPay")
                .paymentStatus("ready")
                .paymentDate(Instant.now())
                .sellerId(sellerId)
                .build();

    }
}
package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderTest orderTestId;

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

    public Payment(OrderTest orderTest, String merchantUid, String impUid, String status, String paymentMethod, Long sellerId) {
        this.orderTestId = orderTest;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.buyerId = orderTest.getBuyerId().getId();
        this.totalAmount = orderTest.getTotalPrice();
        this.paymentMethod = paymentMethod;
        this.paymentStatus = status;
        this.paymentDate = Instant.now();
        this.sellerId = sellerId;

    }

    public static Payment from(OrderTest orderTest, String merchantUid, String tid, Long sellerId) {
        return Payment.builder()
                .orderTestId(orderTest)
                .impUid(tid)
                .merchantUid(merchantUid)
                .buyerId(orderTest.getBuyerId().getId())
                .totalAmount(orderTest.getTotalPrice())
                .paymentMethod("KakaoPay")
                .paymentStatus("ready")
                .paymentDate(Instant.now())
                .sellerId(sellerId)
                .build();

    }
}
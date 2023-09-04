package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

}
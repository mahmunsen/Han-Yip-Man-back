package com.supercoding.hanyipman.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    //TODO: 외래키 걸면 해제
    @OneToMany(mappedBy = "cart")
    private List<CartOptionItem> cartOptionItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    //TODO: 외래키 걸면 해제
    public void add(CartOptionItem cartOptionItem){
        cartOptionItems.add(cartOptionItem);
        cartOptionItem.setCart(this);
    }
    public static Cart from(Buyer buyer, Menu menu, Long amount){
        return Cart.builder()
                .buyer(buyer)
                .menu(menu)
                .amount(amount)
                .build();
    }
    

}
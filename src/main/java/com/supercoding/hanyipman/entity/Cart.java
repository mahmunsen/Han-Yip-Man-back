package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.cart.response.OptionItemResponse;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE cart SET is_deleted = true WHERE id = ?")
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
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToMany(mappedBy = "cart")
    private List<CartOptionItem> cartOptionItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @ColumnDefault(value = "false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;


    public static Cart from(Buyer buyer, Shop shop, Menu menu, Long amount){
        return Cart.builder()
                .buyer(buyer)
                .shop(shop)
                .menu(menu)
                .amount(amount)
                .build();
    }

    public Integer calTotalPrice(){
        Integer price = menu.getPrice();

        if(cartOptionItems == null) return price * amount.intValue();

        price = (price + cartOptionItems.stream()
                .mapToInt(coi -> coi.getOptionItem().getPrice())
                .sum()) * amount.intValue();

        return price;
    }
}
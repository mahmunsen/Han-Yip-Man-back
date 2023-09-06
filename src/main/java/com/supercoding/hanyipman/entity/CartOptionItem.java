package com.supercoding.hanyipman.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_option_item")
public class CartOptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id", nullable = false)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private OptionItem optionItem;

    public static CartOptionItem from(OptionItem optionItem, Cart cart){
        //TODO: 카트 외래키 걸면 해제
        CartOptionItem cartOptionItem = CartOptionItem.builder()
                .optionItem(optionItem)
                .cart(cart)
                .build();
//        new CartOptionItem();
//        cart.add(this);
        return cartOptionItem;
    }

}
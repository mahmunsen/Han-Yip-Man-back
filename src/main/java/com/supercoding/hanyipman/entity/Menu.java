package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "menu_group_id", nullable = false)
    private Long menuGroupId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

}
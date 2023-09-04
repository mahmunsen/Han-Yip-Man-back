package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "menu_group")
public class MenuGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

}
package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "menu_group")
public class MenuGroup {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

}
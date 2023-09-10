package com.supercoding.hanyipman.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "`option`")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "is_multiple", nullable = false)
    private Boolean isMultiple;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

}
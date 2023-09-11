package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuRequest;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_group_id", nullable = false)
    private MenuGroup menuGroup;

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

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "menu",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options  = new ArrayList<>();

    public void addOption(Option option) {
        option.setMenu(this);
        options.add(option);
    }

    public void addOptionList(List<Option> optionList) {

        if (options == null) {
            options = new ArrayList<>();
        }

        optionList.forEach(option -> {
            option.setMenu(this);
            options.add(option);
        });
    }

    public static Menu from(RegisterMenuRequest registerMenuRequest, MenuGroup menuGroup, Integer sequence) {
        return Menu.builder()
                .menuGroup(menuGroup)
                .name(registerMenuRequest.getMenuName())
                .price(registerMenuRequest.getPrice())
                .discountPrice(registerMenuRequest.getPrice())
                .description(registerMenuRequest.getDescription())
                .sequence(sequence+1)
                .isDeleted(false)
                .build();
    }

}
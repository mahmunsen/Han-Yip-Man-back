package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuRequest;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "option_item")
public class OptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public static OptionItem from(RegisterMenuRequest.OptionGroupRequest.OptionItemRequest optionItemRequest) {
        return OptionItem.builder()
                .name(optionItemRequest.getItemName())
                .price(optionItemRequest.getItemPrice())
                .isDeleted(false)
                .build();
    }

}
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
@Table(name = "`option`")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "max_selected")
    private Integer maxSelected;

    @Column(name = "is_multiple", nullable = false)
    private Boolean isMultiple;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "option",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionItem> optionItems = new ArrayList<>();

    public void addOption(OptionItem optionItem) {
        optionItem.setOption(this);
        optionItems.add(optionItem);
    }

    public void addOptionItemList(List<OptionItem> optionItemList) {

        if (optionItems == null) {
            optionItems = new ArrayList<>();
        }

        optionItemList.forEach(optionItem -> {
            optionItem.setOption(this);
            optionItems.add(optionItem);
        });
    }

    public static Option from(RegisterMenuRequest.OptionGroupRequest optionRequest) {
        return Option.builder()
                .name(optionRequest.getOptionName())
                .isMultiple(optionRequest.getIsMultiple())
                .maxSelected(Boolean.TRUE.equals(optionRequest.getIsMultiple()) ? optionRequest.getMaxSelected() : 0)
                .isDeleted(false)
                .build();
    }
}
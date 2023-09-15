package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterShopRequest;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "UPDATE shop SET is_deleted = true WHERE id = ?")
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone_num", nullable = false, length = 20)
    private String phoneNum;

    @Column(name = "min_order_price", nullable = false)
    private Integer minOrderPrice;

    @Column(name = "default_delivery_price", nullable = false)
    private Integer defaultDeliveryPrice;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "business_number")
    private String businessNumber;

    @Lob
    @Column(name = "thumbnail")
    private String thumbnail;

    @Lob
    @Column(name = "banner")
    private String banner;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuGroup> menuGroups = new ArrayList<>();

    @OneToOne(mappedBy = "shop",cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToMany(mappedBy = "shop",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    public void addMenuGroup(MenuGroup menuGroup) {
        menuGroup.setShop(this);
        menuGroups.add(menuGroup);
    }

    public void removeMenuGroup(Long menuGroupId) {
        MenuGroup menuGroup = menuGroups.stream()
                .filter(mg -> mg.getId().equals(menuGroupId))
                .findFirst()
                .orElse(null);
        menuGroups.remove(menuGroup);
        Objects.requireNonNull(menuGroup).setIsDeleted(true);
    }

    public MenuGroup getMenuGroupById(Long menuGroupId) {
        return menuGroups.stream()
                .filter(menuGroup -> menuGroup.getId().equals(menuGroupId))
                .findFirst()
                .orElse(null);
    }

    public void menuGroupUpdateSequenceById(Long menuGroupId, Integer newSequence) {
        MenuGroup menuGroupToUpdate = getMenuGroupById(menuGroupId);

        if (menuGroupToUpdate != null) {
            menuGroupToUpdate.setSequence(newSequence);
        }
    }

    public void menuGroupUpdateNameById(Long menuGroupId, String newName) {
        MenuGroup menuGroupToUpdate = getMenuGroupById(menuGroupId);

        if (menuGroupToUpdate != null) {
            menuGroupToUpdate.setName(newName);
        }
    }

    public void setAddress(Address address) {
        if (address != null) {
            address.setShop(this);
            this.address = address;
        }
    }

    public static Shop from(RegisterShopRequest registerShopRequest, Seller seller, Category category) {
        return Shop.builder()
                .category(category)
                .seller(seller)
                .name(registerShopRequest.getShopName())
                .phoneNum(registerShopRequest.getShopPhone())
                .minOrderPrice(registerShopRequest.getMinOrderPrice())
                .defaultDeliveryPrice(2000)
                .description(registerShopRequest.getShowDescription())
                .businessNumber(registerShopRequest.getBusinessNumber())
                .build();
    }


}
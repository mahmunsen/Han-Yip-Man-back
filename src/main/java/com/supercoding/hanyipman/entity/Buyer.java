package com.supercoding.hanyipman.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buyer")
public class Buyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "profile")
    private String profile;

    @OneToMany(mappedBy = "buyer",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;

    public Address getDefaultAddress() {
        return addresses.stream()
                .filter(address -> address.getIsDefault().equals(true))
                .findFirst()
                .orElse(null);
    }

    public void setAddress(Address address) {
        if (address != null) {
            address.setBuyer(this);
            addresses.add(address);
        }
    }

    public static Buyer tobuyer(User user, String file) {
        return Buyer.builder()
                .user(user)
                .profile(file)
                .build();
    }
}
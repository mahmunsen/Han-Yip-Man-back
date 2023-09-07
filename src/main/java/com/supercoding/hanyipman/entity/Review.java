package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.dto.reivew.response.ShopReview;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Buyer buyer;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Lob
    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    public static ShopReview toShopReview(Review review) {

        return ShopReview.builder()
                .userId(review.getBuyer().getUser().getId())
                .nickName(review.getBuyer().getUser().getNickname())
                .reviewContent(review.getContent())
                .reviewScore(review.getScore())
                .createdAt(review.getCreatedAt())
                .reviewImageUrl(review.getImageUrl())
                .build();
    }

}
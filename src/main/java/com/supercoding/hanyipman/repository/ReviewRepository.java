package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.Review;
import com.supercoding.hanyipman.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBuyerAndShop(Buyer buyer, Shop shop);

    Page<Review> findAllByShopOrderByCreatedAtDesc(Shop shop, Pageable pageable);

    List<Review> findAllByShopAndCreatedAtLessThanOrderByCreatedAtDesc(Shop shop, Instant createdAt, Pageable pageable);

    Page<Review> findAllByShopAndScoreOrderByCreatedAtDesc(Shop shop,Integer score, Pageable pageable);

    List<Review> findAllByShopAndScoreAndCreatedAtLessThanOrderByCreatedAtDesc(Shop shop,Integer score, Instant createdAt, Pageable pageable);
}

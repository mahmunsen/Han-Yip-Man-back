package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.Review;
import com.supercoding.hanyipman.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByBuyerAndShop(Buyer buyer, Shop shop);
}

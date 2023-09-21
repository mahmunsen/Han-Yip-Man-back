package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.BuyerCoupon;
import com.supercoding.hanyipman.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuyerCouponRepository extends JpaRepository<BuyerCoupon, Long> {

    boolean existsBuyerCouponByCouponAndBuyer(Coupon coupon,Buyer buyer);

    List<BuyerCoupon> findBuyerCouponsByBuyerOrderByEnabledDescCreatedAtDesc(Buyer buyer);

    @Query("SELECT bc FROM BuyerCoupon bc " +
            "JOIN fetch bc.coupon c " +
            "WHERE bc.id =:buyerCouponId AND bc.enabled = true")
    Optional<BuyerCoupon> findBuyerCouponFetchCouponByBuyerCouponId(@Param("buyerCouponId") Long buyerCouponId);
}

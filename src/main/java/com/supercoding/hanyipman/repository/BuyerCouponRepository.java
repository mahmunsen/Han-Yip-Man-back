package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.BuyerCoupon;
import com.supercoding.hanyipman.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuyerCouponRepository extends JpaRepository<BuyerCoupon, Long> {

    boolean existsBuyerCouponByCoupon(Coupon coupon);

    List<BuyerCoupon> findBuyerCouponsByBuyerOrderByEnabledDescCreatedAtDesc(Buyer buyer);
}

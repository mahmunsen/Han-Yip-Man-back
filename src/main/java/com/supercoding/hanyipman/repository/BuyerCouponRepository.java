package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.BuyerCoupon;
import com.supercoding.hanyipman.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyerCouponRepository extends JpaRepository<BuyerCoupon, Long> {

    boolean existsBuyerCouponByCoupon(Coupon coupon);
}

package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findCouponByCode(String code);
}

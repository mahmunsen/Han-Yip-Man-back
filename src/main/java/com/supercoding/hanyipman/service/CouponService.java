package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.coupon.request.RegisterCouponRequest;
import com.supercoding.hanyipman.dto.coupon.response.ViewCouponsResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.BuyerCoupon;
import com.supercoding.hanyipman.entity.Coupon;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.CouponErrorCode;
import com.supercoding.hanyipman.repository.BuyerCouponRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final BuyerRepository buyerRepository;
    private final BuyerCouponRepository buyerCouponRepository;

    public void registerCoupon(RegisterCouponRequest request, CustomUserDetail userDetail) {

        Long userId = userDetail.getUserId();
        String couponCode = request.getCouponCode();

        Buyer buyer = validateUser(userId);

        //코드에 해당하는 쿠폰이 존재하는가?
        Coupon coupon = validateCoupon(couponCode);

        // 해당 쿠폰 코드가 등록 된 적이 있나? 있으면 에러 발생, 없으면 새로운 쿠폰 저장,
        if (buyerCouponRepository.existsBuyerCouponByCouponAndBuyer(coupon,buyer))
            throw new CustomException(CouponErrorCode.REGISTERED_BEFORE);

        buyerCouponRepository.save(BuyerCoupon.from(coupon, buyer));

    }

    public List<ViewCouponsResponse> viewCoupons(CustomUserDetail userDetail) {
        Long userId = userDetail.getUserId();

        Buyer buyer = validateUser(userId);

        return buyerCouponRepository.findBuyerCouponsByBuyerOrderByEnabledDescCreatedAtDesc(buyer).stream()
                .map(buyerCoupon -> ViewCouponsResponse.from(buyerCoupon)).collect(Collectors.toList());
    }

    private Buyer validateUser(Long userId) {
        return buyerRepository.findBuyerByUserId(userId)
                .orElseThrow(() -> new CustomException(CouponErrorCode.NOT_PROPER_USER));
    }

    private Coupon validateCoupon(String couponCode) {
        return couponRepository.findCouponByCode(couponCode)
                .orElseThrow(() -> new CustomException(CouponErrorCode.COUPON_NOT_FOUND));
    }
}

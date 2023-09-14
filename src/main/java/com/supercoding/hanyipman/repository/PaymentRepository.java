package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p" +
            " LEFT JOIN fetch p.order o " +
            " WHERE o.id =:orderId ")
    Optional<Payment> findPaymentByOrderId(@Param("orderId") Long orderId);

    Optional<Payment> findPaymentByMerchantUid(String merchantUid);
}

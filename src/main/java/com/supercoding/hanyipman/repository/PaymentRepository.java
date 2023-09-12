package com.supercoding.hanyipman.repository;
import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Optional<Payment> findPaymentByOrder(Order order);
    Payment findPaymentByOrderId(Long orderId);

}

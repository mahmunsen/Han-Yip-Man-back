package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.entity.OrderTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderTestRepository extends JpaRepository<OrderTest, Long> {

    Optional<OrderTest> findOrderTestByBuyerId(Long buyerId);

    Optional<OrderTest> findOrderTestById(Long orderId);
}

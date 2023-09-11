package com.supercoding.hanyipman.repository;
import com.supercoding.hanyipman.entity.OrderTest;
import com.supercoding.hanyipman.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OrderTestRepository extends JpaRepository<OrderTest, Long> {

    Optional<OrderTest> findOrderTestById(Long orderId);

    // 업주 찾기
    @Query("SELECT s FROM Shop p JOIN p.seller s WHERE p.id = :shopId")
    Seller findOrderTestBySeller(@Param("shopId") Long shopId);
}

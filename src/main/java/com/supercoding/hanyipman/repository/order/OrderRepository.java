package com.supercoding.hanyipman.repository.order;

import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.entity.Shop;
import com.supercoding.hanyipman.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderById(Long orderId);
    // 업주 찾기
    @Query("SELECT s FROM Shop p JOIN p.seller s WHERE p.id = :shopId")
    Optional<Seller> findSellerByShopId(@Param("shopId") Long shopId);

    @Query("SELECT o FROM Order o " +
            "JOIN fetch o.shop s " +
            "JOIN fetch o.buyer " +
            "WHERE o.id =:orderId")
    Optional<Order> findOrderFetchShopAndFetchBuyerByOrderId(@Param("orderId")Long orderId);

    @Query("SELECT o FROM Order o " +
            "JOIN fetch o.buyer b " +
            "WHERE o.id =:orderId ")
    Optional<Order> findOrderFetchBuyerByOrderId(@Param("orderId") Long orderId);


    @Query("SELECT o FROM Order o WHERE o.shop = :shop AND o.orderStatus IN (:orderStatusList) ORDER BY o.orderStatus")
    Optional<List<Order>> findOrderExceptCompleted(Shop shop, List<OrderStatus> orderStatusList);

    @Query("SELECT o FROM Order o " +
            "JOIN fetch o.buyer b " +
            "WHERE o.id =:orderId" +
            " AND o.isDeleted = false")
    Optional<Order> findOrderByIdAndIsDeletedFalse(@Param("orderId") Long orderId);

}

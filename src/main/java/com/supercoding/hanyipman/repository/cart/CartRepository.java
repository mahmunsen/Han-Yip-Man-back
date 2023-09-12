package com.supercoding.hanyipman.repository.cart;

import com.supercoding.hanyipman.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c " +
            " join c.menu m " +
            " join c.buyer b " +
            "where (c.isDeleted = false or c.isDeleted is null) " +
            "and b.id =:buyerId")
    Page<Cart> findCartsByUnpaidCart(@Param("buyerId") Long buyerId, Pageable pageable);

    @Query("SELECT c FROM Cart c " +
            "JOIN fetch c.shop s " +
            "JOIN fetch c.buyer b " +
            "WHERE c.id =:cartId " +
            "and (c.isDeleted is null or c.isDeleted is false )")
    Optional<Cart> findCartByCartId(@Param("cartId") Long cartId);

    @Query("SELECT c FROM Cart c " +
            "JOIN fetch c.shop s " +
            "JOIN fetch c.buyer b " +
            "WHERE b.id =:buyerId " +
            "AND (c.isDeleted is null or c.isDeleted is false)")
    List<Cart> findCartsByBuyerId(@Param("buyerId") Long buyerId);

    @Query("SELECT c FROM Cart c " +
            "JOIN fetch c.shop s " +
            "JOIN fetch c.buyer b " +
            "JOIN fetch c.order o " +
            "JOIN fetch c.menu m " +
            "WHERE o.id in :ordersId")
    List<Cart> findCartsByOrderId(@Param("ordersId")List<Long> ordersId);
}

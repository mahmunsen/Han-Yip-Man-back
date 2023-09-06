package com.supercoding.hanyipman.repository;

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

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT c FROM Cart c " +
            " join c.menu m " +
            " join c.buyer b " +
            "where (c.isDeleted = false or c.isDeleted is null) " +
            "and b.id =:buyerId")
    Page<Cart> findCartsByUnpaidCart(@Param("buyerId") Long buyerId, Pageable pageable);

}

package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.CartOptionItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public interface CartOptionItemRepository extends JpaRepository<CartOptionItem, Long> {

    @Query("SELECT coi FROM CartOptionItem coi WHERE coi.id =:cartId")
    List<CartOptionItem> findCartOptionItemsByCartId(@Param("cartId") Long cartId);

    @Query("SELECT coi " +
            "FROM CartOptionItem coi " +
            "JOIN fetch coi.optionItem oi " +
            "JOIN fetch coi.cart c " +
            "WHERE c.id in :cartIds")
    List<CartOptionItem> findCartOptionItemsByCartIds(@Param("cartIds") List<Long> cartIds);
}

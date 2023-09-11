package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.MenuGroup;
import com.supercoding.hanyipman.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MenuGroupRepository extends JpaRepository<MenuGroup, Long> {

    @Query("SELECT COALESCE(MAX(mg.sequence), 0) FROM MenuGroup mg WHERE mg.shop = :shop")
    Integer findMaxSequenceByShop(@Param("shop") Shop shop);

    Optional<MenuGroup> findByIdAndIsDeletedFalse(Long menuGroupId);

}

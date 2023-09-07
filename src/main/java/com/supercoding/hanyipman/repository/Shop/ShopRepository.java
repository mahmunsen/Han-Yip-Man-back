package com.supercoding.hanyipman.repository.Shop;

import com.supercoding.hanyipman.dto.shop.buyer.response.ViewShopListResponse;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopCustomRepository {

    @Query("SELECT s FROM Shop s WHERE s.id =:shopId")
    Optional<Shop> findShopByShopId(@Param("shopId") Long shopId);

    List<Shop> findAllBySellerAndIsDeletedFalse(Seller seller);
}

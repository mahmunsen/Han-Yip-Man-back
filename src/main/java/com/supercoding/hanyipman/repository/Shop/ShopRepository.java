package com.supercoding.hanyipman.repository.Shop;

import com.supercoding.hanyipman.dto.shop.buyer.response.ViewShopListResponse;
import com.supercoding.hanyipman.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShopRepository extends JpaRepository<Shop, Long>, ShopCustomRepository {

}

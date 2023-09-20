package com.supercoding.hanyipman.repository.menu;

import com.supercoding.hanyipman.entity.Menu;

import java.util.Optional;

public interface MenuCustomRepository {

    Optional<Menu> findMenuByShop(Long shopId, Long menuId);
}

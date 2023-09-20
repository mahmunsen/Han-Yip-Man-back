package com.supercoding.hanyipman.repository.menu;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.supercoding.hanyipman.entity.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.supercoding.hanyipman.entity.QShop.shop;
import static com.supercoding.hanyipman.entity.QMenuGroup.menuGroup;
import static com.supercoding.hanyipman.entity.QMenu.menu;

@Repository
@RequiredArgsConstructor
public class MenuCustomRepositoryImpl implements MenuCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Optional<Menu> findMenuByShop(Long shopId, Long menuId) {

        Menu fetchMenu = jpaQueryFactory.select(menu)
                .from(shop)
                .join(shop.menuGroups, menuGroup)
                .join(menuGroup.menus, menu)
                .where(
                        shop.id.eq(shopId),
                        menu.id.eq(menuId),
                        menu.isDeleted.eq(false)
                )
                .fetchOne();

        return Optional.ofNullable(fetchMenu);
    }
}

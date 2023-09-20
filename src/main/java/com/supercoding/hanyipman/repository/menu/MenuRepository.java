package com.supercoding.hanyipman.repository.menu;

import com.supercoding.hanyipman.entity.Menu;
import com.supercoding.hanyipman.entity.MenuGroup;
import com.supercoding.hanyipman.entity.Shop;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>, MenuCustomRepository {
    @Query("SELECT m FROM Menu m where m.id =:menuId")
    Optional<Menu> findMenuByMenuId(@Param("menuId") Long menuId);

    @Query("SELECT m FROM Menu m where m.id =:menuId AND m.isDeleted = false")
    Optional<Menu> findByMenuIsDeletedFalse(Long menuId);

    @Query("SELECT COALESCE(MAX(m.sequence), 0) FROM Menu m WHERE m.menuGroup = :menuGroup")
    Integer findMaxSequenceByShop(@Param("menuGroup") MenuGroup menuGroup);

    List<Menu> findAllByMenuGroupAndIsDeletedFalse(MenuGroup menuGroup);

}

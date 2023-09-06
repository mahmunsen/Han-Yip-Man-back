package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    @Query("SELECT m FROM Menu m where m.id =:menuId")
    Optional<Menu> findMenuByMenuId(@Param("menuId") Long menuId);
}

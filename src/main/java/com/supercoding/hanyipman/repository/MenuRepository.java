package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MenuRepository {
    private final EntityManager em;

    public Optional<Menu> findMenuByMenuId(Long menuId) {
        return Optional.ofNullable(em.createQuery("SELECT m FROM Menu m where m.id =: menuId", Menu.class)
                .setParameter("menuId", menuId)
                .getSingleResult());
    }
}

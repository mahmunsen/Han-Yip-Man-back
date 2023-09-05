package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@Repository
public class CartRepository {
    private final EntityManager em;

    public void save(Cart cart) {
        if(cart.getId() == null) {
            em.persist(cart);
        }else{
            em.merge(cart);
        }
    }
}

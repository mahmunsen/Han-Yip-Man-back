package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.dto.vo.CustomPageable;
import com.supercoding.hanyipman.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.awt.print.Pageable;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class EmCartRepository {
    private final EntityManager em;


    public List<Cart> findCartsByUnpaidCart(Long buyerId, CustomPageable pageable){
        return em.createQuery(
                        "SELECT c FROM Cart c " +
                                " join fetch c.menu m " +
                                " join fetch c.buyer b " +
                                " where (c.isDeleted = false or c.isDeleted is null) " +
                                " and b.id =:buyerId", Cart.class)
                .setParameter("buyerId", buyerId)
                .setFirstResult(pageable.getStartIndex())
                .setMaxResults(pageable.getEndIndex())
                .getResultList();
    }

    public void saveCarts(List<Cart> carts) {
        carts.forEach(this::save);
    }

    public void save(Cart cart) {
        if(cart.getId() == null) {
            em.persist(cart);
        }else{
            em.merge(cart);
        }
    }
}

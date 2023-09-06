package com.supercoding.hanyipman.repository;

import com.supercoding.hanyipman.entity.CartOptionItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CartOptionItemRepository {
    private final EntityManager em;
    public void saveCartOptionItems(List<CartOptionItem> cartOptionItems) {
        cartOptionItems.forEach(this::save);
    }

    public void save(CartOptionItem cartOptionItem){
        if(cartOptionItem.getId() == null) {
            em.persist(cartOptionItem);
        }else{
            em.merge(cartOptionItem);
        }
    }
}

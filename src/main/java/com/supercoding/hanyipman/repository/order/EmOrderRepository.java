package com.supercoding.hanyipman.repository.order;

import com.supercoding.hanyipman.dto.order.response.ViewOrderResponse;
import com.supercoding.hanyipman.dto.vo.CustomPageable;
import com.supercoding.hanyipman.dto.vo.PageResponse;
import com.supercoding.hanyipman.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class EmOrderRepository {
    private final EntityManager em;
    public List<Order> findListOrders(Long buyerId, CustomPageable pageable) {
        return em.createQuery(
                        "SELECT o FROM Order o " +
                                "JOIN fetch o.shop " +
                                "JOIN fetch o.address " +
                                "JOIN fetch o.buyer b " +
                                "WHERE b.id =:buyerId", Order.class)
                .setParameter("buyerId", buyerId)
                .setFirstResult(pageable.getStartIndex())
                .setMaxResults(pageable.getEndIndex())
                .getResultList();
    }
}

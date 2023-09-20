package com.supercoding.hanyipman.repository.order;

import com.supercoding.hanyipman.dto.order.response.ViewOrderResponse;
import com.supercoding.hanyipman.dto.vo.CustomPageable;
import com.supercoding.hanyipman.dto.vo.PageResponse;
import com.supercoding.hanyipman.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class EmOrderRepository {
    private final EntityManager em;
    public List<Order> findListOrders(Long buyerId, CustomPageable pageable) {

        return em.createQuery("SELECT o FROM Order o " +
                        "JOIN fetch o.shop s " +
                        "JOIN fetch o.address " +
                        "JOIN fetch o.buyer b " +
                        "WHERE o.id < :cursor " +
                        "AND b.id =:buyerId " +
                        "ORDER BY o.id DESC ", Order.class)
                .setParameter("buyerId", buyerId)
                .setParameter("cursor", pageable.getCursor())
                .setFirstResult(0)
                .setMaxResults(pageable.getSize())
                .getResultList();
    }

    public Optional<Order> findOrderByOrderId(Long orderId) {
        return Optional.ofNullable(em.createQuery("SELECT o FROM Order o " +
                        "JOIN fetch o.shop s " +
                        "JOIN fetch o.address " +
                        "JOIN fetch o.buyer b " +
                        "WHERE o.id =:orderId ", Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult());

    }
}

package com.supercoding.hanyipman.entity;

import com.supercoding.hanyipman.enums.OrderStatus;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.OrderErrorCode;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.supercoding.hanyipman.enums.OrderStatus.WAIT;

@Getter
@Setter
@Entity
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted is null or is_deleted = false")
@SQLDelete(sql = "UPDATE `order` SET is_deleted = true WHERE id = ?")
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_uid", nullable = false)
    private String orderUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_coupon_id")
    private BuyerCoupon buyerCoupon;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private OrderStatus orderStatus;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice = 0;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @OneToMany(mappedBy="order")
    private List<Cart> carts = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false)
    private Instant updatedAt;

    @ColumnDefault(value = "false")
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name="sequence")
    private Integer orderSequence;

    public static Order from(Buyer buyer, String orderUid, Address address, Shop shop, BuyerCoupon buyerCoupon, List<Cart> carts){

        Order order = Order.builder()
                .buyer(buyer)
                .orderUid(orderUid)
                .address(address)
                .shop(shop)
                .orderStatus(WAIT)
                .carts(new ArrayList<>())
                .buyerCoupon(buyerCoupon)
                .isDeleted(false)
                .build();
        // 총 금액 계산
        carts.forEach(order::add);
        order.calTotalPrice();

        return order;
    }

    // 주문 테이블과 Cart를 연결한다는 건 결제를 한다는 의미이기 때문에 여기서 장바구니 삭제 로직을 처리함
    public void add(Cart cart){
        carts.add(cart);
        cart.setOrder(this);
        cart.setIsDeleted(true);
    }

    public void calTotalPrice(){
        // 계산할 메뉴 없음
        if(carts == null) return;

        // 장바구니에 담긴 음식 총 금액 계산
        Integer totalPrice =  carts.stream().mapToInt(Cart::calTotalPrice).sum();

        // 최소 주문 금액 확인
        if(totalPrice < shop.getMinOrderPrice()) throw new CustomException(OrderErrorCode.ORDER_MIN_PRICE);

        // 할인 적용
        if(this.buyerCoupon != null) totalPrice -= this.buyerCoupon.discount();

        // 배달비 추가
        totalPrice += shop.getDefaultDeliveryPrice();

        this.totalPrice = totalPrice;
    }

}
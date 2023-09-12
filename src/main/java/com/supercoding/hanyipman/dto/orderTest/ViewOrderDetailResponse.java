package com.supercoding.hanyipman.dto.orderTest;
import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.entity.Payment;
import com.supercoding.hanyipman.entity.Shop;
import com.supercoding.hanyipman.utils.DateUtils;
import lombok.*;

import java.text.ParseException;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewOrderDetailResponse {

    private String shopName;
    //    private String orderName; // todo 주문 메뉴명
    private String createdAt;
    private String orderUid;
    private Integer totalPrice;
    private String orderStatus;
    //    private Map<String, String> address;
    private String address;
    private String payMethod;
    private String phoneNum;
    private String shopTelphoneNum;

    public ViewOrderDetailResponse toDto(Order order, Payment payment, String deliveryAddress, Shop shop) throws ParseException {
        return ViewOrderDetailResponse.builder()
                .shopName(shop.getName())
                .createdAt(DateUtils.convertToString(payment.getPaymentDate()))
                .orderUid(order.getOrderUid())
                .totalPrice(payment.getTotalAmount())
                .orderStatus(order.getOrderStatus().getStatus())
                .address(deliveryAddress)
                .payMethod(payment.getPaymentMethod())
                .phoneNum(order.getBuyer().getUser().getPhoneNum())
                .shopTelphoneNum(shop.getPhoneNum())
                .build();
    }
}

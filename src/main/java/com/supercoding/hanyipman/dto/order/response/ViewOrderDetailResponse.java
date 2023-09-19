package com.supercoding.hanyipman.dto.order.response;

import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.entity.Payment;
import com.supercoding.hanyipman.entity.Shop;
import com.supercoding.hanyipman.utils.DateUtils;
import com.supercoding.hanyipman.utils.PhoneUtils;
import lombok.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import static com.supercoding.hanyipman.utils.DateUtils.yearMonthDayHourMinuteSecond;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewOrderDetailResponse {

    private String orderUid;
    private String createdAt;
    private String shopName;
    private Long shopId; // 가게 아이디
    private String orderName; // 주문한 메뉴명
    private List<Map<String, Object>> orderMenus; // 메뉴들, 수량(첫번째) 가격(둘째) 옵션들 (셋째)
    private Integer defaultDeliveryPrice;
    private Integer totalPrice;
    private Integer buyerCouponDiscount;
    private String orderStatus;
    private String address;
    private String payMethod;
    private String phoneNum;
    private String shopTelphoneNum;
    private String canceledAt;

    public ViewOrderDetailResponse toDto(Order order, Payment payment, String deliveryAddress, Shop shop, List<Map<String, Object>> orderMenus, String orderName) throws ParseException {
        return ViewOrderDetailResponse.builder()
                .shopId(shop.getId())
                .shopName(shop.getName())
                .orderName(orderName)
                .orderMenus(orderMenus)
                .createdAt(DateUtils.convertToString(payment.getPaymentDate(), yearMonthDayHourMinuteSecond))
                .orderUid(order.getOrderUid())
                .totalPrice(payment.getTotalAmount())
                .buyerCouponDiscount(order.getBuyerCoupon() == null ? 0 : order.getBuyerCoupon().discount())
                .defaultDeliveryPrice(shop.getDefaultDeliveryPrice())
                .orderStatus(order.getOrderStatus().getStatus())
                .address(deliveryAddress)
                .payMethod(payment.getPaymentMethod())
//              .phoneNum(order.getBuyer().getUser().getPhoneNum())
                .phoneNum(PhoneUtils.formattedPhoneNumber(order.getBuyer().getUser().getPhoneNum()))
                .shopTelphoneNum(shop.getPhoneNum())
                .canceledAt(order.getOrderStatus().getStatus() == "CANCELED" ? DateUtils.convertToString(payment.getCancellationDate(), yearMonthDayHourMinuteSecond) : null)
                .build();
    }
}

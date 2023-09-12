package com.supercoding.hanyipman.dto.order.response;

import com.supercoding.hanyipman.dto.Shop.seller.response.MenuResponse;
import com.supercoding.hanyipman.entity.Cart;
import com.supercoding.hanyipman.entity.CartOptionItem;
import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.supercoding.hanyipman.utils.DateUtils.yearMonthDayHourMinuteSecond;

@Getter
@Builder
@AllArgsConstructor
public class ViewOrderResponse {
    private final String orderUid;
    private final String shopName;
    private final String bannerImg;
    private final List<String> menus;
    private final List<String> options;
    private final Integer totalPrice;
    private final String orderDateTime;
    private final String orderStatus;
    public static ViewOrderResponse from(Order order){
        return ViewOrderResponse.builder()
                .orderUid(order.getOrderUid())
                .shopName(order.getShop().getName())
                .bannerImg(order.getShop().getBanner())
                .menus(convertMenuNames(order))
                .options(convertOptionNames(order))
                .totalPrice(order.getTotalPrice())
                .orderDateTime(DateUtils.convertToString(order.getCreatedAt(), yearMonthDayHourMinuteSecond))
                .orderStatus(order.getOrderStatus().getStatus())
                .build();
    }

    private static List<String> convertMenuNames(Order order) {
        return order.getCarts().stream()
                .map(cart -> convertNameAndAmount(cart.getMenu().getName(), cart.getAmount().intValue())).collect(Collectors.toList());
    }

    private static List<String> convertOptionNames(Order order) {
        return order.getCarts().stream()
                .filter(cart -> cart.getCartOptionItems() != null)
                .map(cart -> cart.getCartOptionItems()
                        .stream().filter(coi -> coi.getOptionItem().getName() != null)
                        .map(coi -> convertNameAndAmount(coi.getOptionItem().getName(), cart.getAmount().intValue()))
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.toList());
    }

    private static String convertNameAndAmount(String name, Integer amount) {
        return name + " x " + amount;
    }

//    private static List<String> convertMenuNames(Order order) {
//        return
//    }
}

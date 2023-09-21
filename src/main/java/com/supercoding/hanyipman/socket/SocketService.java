package com.supercoding.hanyipman.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.supercoding.hanyipman.dto.order.response.OrderNoticeBuyerResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.dto.websocket.ChatMessage;
import com.supercoding.hanyipman.dto.websocket.MessageType;
import com.supercoding.hanyipman.dto.websocket.OrderStatusMessage;
import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.entity.Shop;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.enums.EventName;
import com.supercoding.hanyipman.enums.OrderStatus;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.OrderErrorCode;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.error.domain.WebSocketErrorCode;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.repository.order.OrderRepository;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import com.supercoding.hanyipman.service.OrderService;
import com.supercoding.hanyipman.service.SseEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {
    private final OrderRepository orderRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final SseEventService sseEventService;

    public void sendMessage(String room, String eventName, SocketIOClient senderClient, String message, String token) {
        try {
            validateToken(token, senderClient);
            String userEmail = jwtTokenProvider.getUserEmail(token);
            User user = validateUser(userEmail);
            for (
                    SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
                if (!client.getSessionId().equals(senderClient.getSessionId())) {
                    client.sendEvent(eventName,
                            new ChatMessage(MessageType.SERVER, message, user.getNickname()));
                }
            }
        } catch (CustomException e) {
            sendErrorMessage(e.getErrorCode().getCode(), e.getErrorMessage(), senderClient);
        }


    }

    @Transactional
    public OrderStatusMessage sendOrderStatus(OrderStatusMessage data, String eventName, SocketIOClient senderClient, String token
    ) {
        Long orderId = data.getOrderId();
        Integer orderSequence = data.getOrderSequence();
        OrderStatus orderStatus = data.getOrderStatus();

        try {
            validateToken(token, senderClient);
            String userEmail = jwtTokenProvider.getUserEmail(token);
            User user = validateUser(userEmail);
            Order order = validateOrder(orderId);

            String storeName = order.getShop().getName();
            String maxPriceMenuName = order.getCarts().stream()
                    .max(Comparator.comparingInt(cart -> cart.getMenu().getPrice()))
                    .map(cart -> cart.getMenu().getName()).orElse("메뉴명");

            setOrderSequence(order, orderSequence, orderStatus);

            //고객이 이미 취소한 건에 대해 사장이 주문 취소 요청 시 에러
            if (order.getOrderStatus().equals(OrderStatus.CANCELED))
                throw new CustomException(OrderErrorCode.ORDER_ALREADY_CANCELED);

            changeOrderStatus(order, orderStatus, user.getId());

            OrderStatusMessage orderStatusMessage
                    = new OrderStatusMessage(orderStatus, "주문 상태가 정상 변경되었습니다.", order.getId(), storeName, maxPriceMenuName, orderSequence);
            OrderNoticeBuyerResponse orderNoticeBuyerResponse = orderService.findOrderNoticeToBuyer(order.getBuyer().getUser().getId(), orderId);
            sseEventService.validSendMessage(order.getBuyer().getUser().getId(), EventName.NOTICE_ORDER, orderNoticeBuyerResponse);


            for (SocketIOClient client : senderClient.getNamespace().getRoomOperations("order" + orderId).getClients()) {
                client.sendEvent(eventName, orderStatusMessage);
            }
            return orderStatusMessage;
        } catch (CustomException e) {
            sendErrorMessage(e.getErrorCode().getCode(), e.getErrorMessage(), senderClient);
            return new OrderStatusMessage(false, e.getErrorCode().getCode(), e.getErrorMessage());
        }

    }

    public boolean validateToken(String token, SocketIOClient client) {
        boolean checkToken = false;
        try {
            if (jwtTokenProvider.validateToken(token)) {
                client.set("token", token);
                checkToken = true;
            }
        } catch (CustomException e) {
            sendErrorMessage(e.getErrorCode().getCode(), e.getErrorMessage(), client);
//            TODO : 토큰 정보 맞지 않았을 때 어떻게 소켓 끊고 응답 보낼것인가.
//            client.disconnect();
        }
        return checkToken;
    }

    private Order validateOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new CustomException(WebSocketErrorCode.ORDER_NOT_EXIST));

    }

    private User validateUser(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
    }

    private void changeOrderStatus(Order order, com.supercoding.hanyipman.enums.OrderStatus orderStatus, Long userId) {

        if (orderStatus.equals("CANCELED")) orderService.IntentionalCancelOrder(order.getId(), userId);
        order.setOrderStatus(orderStatus);
    }

    private void sendErrorMessage(Integer errorCode, String errorMessage, SocketIOClient senderClient) {
        senderClient.sendEvent("get_error", new Response(false, errorCode, errorMessage, null));
        log.error("\u001B[31mcode: " + errorCode + "\u001B[0m");
        log.error("\u001B[31mmessage: " + errorMessage + "\u001B[0m");
    }

    private void setOrderSequence(Order order, Integer orderPositionToChange, OrderStatus orderStatus) {
        Shop shop = order.getShop();
        //요청 전 위치와 주문 상태
        Integer oldPosition = order.getOrderSequence();
        OrderStatus oldOrderStatus = order.getOrderStatus();

        if (orderStatus.equals(oldOrderStatus)) {
            if (oldPosition > orderPositionToChange) {
                List<Order> ordersToChangePosition = orderRepository.findByOrderStatusAndShopAndOrderSequenceBetween(orderStatus, shop, orderPositionToChange, oldPosition - 1);
                for (Order o : ordersToChangePosition) {
                    o.setOrderSequence(o.getOrderSequence() + 1);
                }
            } else if (orderPositionToChange > oldPosition) {
                List<Order> ordersToChangePosition = orderRepository.findByOrderStatusAndShopAndOrderSequenceBetween(orderStatus, shop, oldPosition + 1, orderPositionToChange);
                for (Order o : ordersToChangePosition) {
                    o.setOrderSequence(o.getOrderSequence() - 1);
                }
            }
            order.setOrderSequence(orderPositionToChange);
        } else {
            List<Order> ordersToChangePosition = orderRepository.findByOrderStatusAndShopAndOrderSequenceAfter(oldOrderStatus, shop, oldPosition);
            for (Order o : ordersToChangePosition) {
                o.setOrderSequence(o.getOrderSequence() - 1);
            }
            List<Order> ordersToChangePositionInNewOrderStatus = orderRepository.findByOrderStatusAndShopAndOrderSequenceAfter(orderStatus, shop, orderPositionToChange - 1);
            for (Order o : ordersToChangePositionInNewOrderStatus) {
                o.setOrderSequence(o.getOrderSequence() + 1);
            }
            order.setOrderSequence(orderPositionToChange);
        }


    }

}
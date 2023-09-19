package com.supercoding.hanyipman.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.dto.websocket.ChatMessage;
import com.supercoding.hanyipman.dto.websocket.MessageType;
import com.supercoding.hanyipman.dto.websocket.OrderStatusMessage;
import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.enums.OrderStatus;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.OrderErrorCode;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.error.domain.WebSocketErrorCode;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.repository.order.OrderRepository;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import com.supercoding.hanyipman.service.OrderService;
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
    public void sendOrderStatus(OrderStatusMessage data, String eventName, SocketIOClient senderClient, String token
    ) {
        String room = data.getRoom();
        Integer orderPosition = data.getOrderPosition();
        OrderStatus orderStatus = data.getOrderStatus();

        try {
            validateToken(token, senderClient);
            String userEmail = jwtTokenProvider.getUserEmail(token);
            User user = validateUser(userEmail);
            Order order = validateOrder(Long.valueOf(room.substring(5)));
            String storeName = order.getShop().getName();
            String orderMenuName = order.getCarts().stream()
                    .max(Comparator.comparingInt(cart -> cart.getMenu().getPrice()))
                    .map(cart -> cart.getMenu().getName()).orElse("메뉴명");

            setOrderPosition(order, orderPosition, orderStatus);

            OrderStatusMessage orderStatusMessage = new OrderStatusMessage(orderStatus, "주문 상태가 정상 변경되었습니다.", order.getId(), storeName, orderMenuName);

            if (!order.getOrderStatus().equals(OrderStatus.CANCELED)) {
                changeOrderStatus(order, orderStatus, user.getId());
            } else {
                //고객이 이미 취소한 건에 대해 사장이 주문 취소 요청 시 에러
                throw new CustomException(OrderErrorCode.ORDER_ALREADY_CANCELED);
            }
            for (SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
                client.sendEvent(eventName, orderStatusMessage);
            }
            senderClient.sendEvent(eventName, orderStatusMessage);
        } catch (CustomException e) {
            sendErrorMessage(e.getErrorCode().getCode(), e.getErrorMessage(), senderClient);
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
            client.disconnect();
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

    private void setOrderPosition(Order order, Integer orderPositionToChange, OrderStatus orderStatus) {
        Seller seller = order.getShop().getSeller();
        List<Order> ordersInOrderStatus = orderRepository.findOrdersBySellerAndOrderStatus(seller, orderStatus).orElseThrow(null);
        if (ordersInOrderStatus == null) {
            order.setOrderSequence(1);
        } else {
            Integer oldPosition = order.getOrderSequence();
            if (oldPosition > orderPositionToChange) {
                List<Order> ordersToChangePosition = orderRepository.findByOrderStatusAndOrderPositionBetween(orderStatus, orderPositionToChange, oldPosition - 1);
                for (Order o : ordersToChangePosition) {
                    o.setOrderSequence(o.getOrderSequence() + 1);
                }
            } else if (orderPositionToChange > oldPosition) {
                List<Order> ordersToChangePosition = orderRepository.findByOrderStatusAndOrderPositionBetween(orderStatus, oldPosition + 1, orderPositionToChange);
                for (Order o : ordersToChangePosition) {
                    o.setOrderSequence(o.getOrderSequence() - 1);
                }
            }
            order.setOrderSequence(orderPositionToChange);
        }

    }

}
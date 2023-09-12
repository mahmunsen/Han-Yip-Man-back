package com.supercoding.hanyipman.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.dto.websocket.ChatMessage;
import com.supercoding.hanyipman.dto.websocket.MessageType;
import com.supercoding.hanyipman.dto.websocket.OrderStatusMessage;
import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.WebSocketErrorCode;
import com.supercoding.hanyipman.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {
    private final OrderRepository orderRepository;
//    private final OrderService orderService;

    public void sendMessage(String room, String eventName, SocketIOClient senderClient, String message) {
        for (
                SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
            if (!client.getSessionId().equals(senderClient.getSessionId())) {
                client.sendEvent(eventName,
                        new ChatMessage(MessageType.SERVER, message));
            }
        }
    }

    @Transactional
    public void sendOrderStatus(String room, String eventName, SocketIOClient senderClient, com.supercoding.hanyipman.enums.OrderStatus orderStatus
    ) {
        Long orderId = Long.valueOf(room);

        try {
            Order order = validateOrder(orderId);
            if (!order.getOrderStatus().equals("CANCELED")) {
                changeOrderStatus(order, orderStatus);
            } else {
                //고객이 이미 취소한 건에 대해 사장이 주문 취소 요청
                sendErrorMessage(HttpStatus.CONFLICT.value(),"해당 주문이 이미 취소되었습니다.",senderClient);
            }
        } catch (CustomException e) {
            sendErrorMessage(e.getErrorCode().getCode(),e.getErrorMessage(),senderClient);
        }
        //고객이 취소하지 않았을 경우, 사장이 주문 취소 요청


        for (
                SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
            if (!client.getSessionId().equals(senderClient.getSessionId())) {
                client.sendEvent(eventName,
                        new OrderStatusMessage(orderStatus, "주문 상태가 정상 변경되었습니다."));
            }
        }

    }

    private Order validateOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new CustomException(WebSocketErrorCode.ORDER_NOT_EXIST));

    }

    private void changeOrderStatus(Order order, com.supercoding.hanyipman.enums.OrderStatus orderStatus) {

        //TODO 주문 취소 메서드 추후 변경
//        if(orderStatusTypeToChange.equals("CANCELED")) orderService.cancelOrder();
        order.setOrderStatus(orderStatus);
    }

    private void sendErrorMessage(Integer errorCode, String errorMessage, SocketIOClient senderClient) {
        senderClient.sendEvent("get_error", new Response(false,errorCode,errorMessage, null));
        log.error("\u001B[31mcode: " + errorCode + "\u001B[0m");
        log.error("\u001B[31mmessage: " +errorMessage + "\u001B[0m");
    }

}
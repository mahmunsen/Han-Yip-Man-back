package com.supercoding.hanyipman.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.supercoding.hanyipman.dto.websocket.ChatMessage;
import com.supercoding.hanyipman.dto.websocket.OrderStatusMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketController {

    private final SocketIOServer server;
    private final SocketService socketService;

    public SocketController(SocketIOServer server, SocketService socketService) {
        this.server = server;
        this.socketService = socketService;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("send_message", ChatMessage.class, onChatReceived());
        server.addEventListener("send_order", OrderStatusMessage.class, onOrderStatusReceived());


    }

    private DataListener<ChatMessage> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendMessage(data.getRoom(),"get_message", senderClient, data.getMessage());
        };
    }

    private DataListener<OrderStatusMessage> onOrderStatusReceived() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendOrderStatus(data.getRoom(),"get_order_status_response", senderClient, data.getOrderStatus());
        };
    }

    private ConnectListener onConnected() {
        return (client) -> {
            String room = client.getHandshakeData().getSingleUrlParam("room");
            log.info("room : " + room);

            client.joinRoom(room);
            log.info("Socket ID[{}]  Connected to socket", client.getSessionId().toString());
        };

    }



    private DisconnectListener onDisconnected() {
        return (client) -> {
            log.info("Client[{}] - Disconnected from socket", client.getSessionId().toString());
        };
    }

}
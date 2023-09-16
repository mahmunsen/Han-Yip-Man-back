package com.supercoding.hanyipman.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.supercoding.hanyipman.dto.websocket.ChatMessage;
import com.supercoding.hanyipman.dto.websocket.OrderStatusMessage;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SocketController {

    private final SocketIOServer server;
    private final SocketService socketService;
    private final JwtTokenProvider jwtTokenProvider;

    public SocketController(SocketIOServer server, SocketService socketService, JwtTokenProvider jwtTokenProvider) {
        this.server = server;
        this.socketService = socketService;
        this.jwtTokenProvider = jwtTokenProvider;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("send_message", ChatMessage.class, onChatReceived());
        server.addEventListener("send_order_status_change", OrderStatusMessage.class, onOrderStatusReceived());
        server.addEventListener("room_enter", String.class, onRoomEnterReceived());


    }

    private DataListener<String> onRoomEnterReceived() {
        return (senderClient, data, ackSender) -> {
            senderClient.joinRoom(data);
        };
    }
    private DataListener<ChatMessage> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
            socketService.sendMessage(data.getRoom(),"get_message", senderClient, data.getMessage(), senderClient.get("token"));
        };
    }

    private DataListener<OrderStatusMessage> onOrderStatusReceived() {
        return (senderClient, data, ackSender) -> {
            socketService.sendOrderStatus(data.getRoom(),"get_order_status_change", senderClient, data.getOrderStatus(), senderClient.get("token"));
        };
    }

    private ConnectListener onConnected() {
        return (client) -> {
            String token = client.getHandshakeData().getSingleUrlParam("token").substring(7);
            if (socketService.validateToken(token, client)) {
                log.info("Socket ID[{}]  Connected to socket", client.getSessionId().toString());
            }
        };

    }

    private DisconnectListener onDisconnected() {
        return (client) -> {
            log.info("Client[{}] - Disconnected from socket", client.getSessionId().toString());
        };
    }

}
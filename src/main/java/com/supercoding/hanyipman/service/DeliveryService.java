package com.supercoding.hanyipman.service;


import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.supercoding.hanyipman.dto.delivery.response.DeliveryLocation;
import com.supercoding.hanyipman.dto.order.response.OrderNoticeResponse;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.Order;
import com.supercoding.hanyipman.enums.OrderStatus;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.error.domain.OrderErrorCode;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.order.EmOrderRepository;
import com.supercoding.hanyipman.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.supercoding.hanyipman.enums.EventName.NOTICE_DRONE_LOCATION;
import static com.supercoding.hanyipman.enums.EventName.NOTICE_ORDER_SELLER;


@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {
    private final BuyerRepository buyerRepository;
    private final OrderService orderService;
    private final EmOrderRepository orderRepository;
    private final OrderRepository orderRepositoryToSave;
    private final SseMessageService sseMessageService;
    private final AddressRepository addressRepository;
    private final Integer END_TIME = 60;
    private final SseEventService sseEventService;
    private final SocketIOServer socketIOServer;

    @Async
    @Transactional
    public void getDeliveryLocation(Long buyerId,Long sellerId, Long orderId) {
        Order order = findOrderByOrderId(orderId);
        Address startAddress = order.getShop().getAddress();
        Address endAddress = order.getAddress();
        findBuyerByUserId(buyerId);

        // 1차 구현 시작지점, 끝 지점 둘 다 +, + 라 가정하고 작성 추후 변경 예정
        Double startLatitude = startAddress.getLatitude();
        Double startLongitude = startAddress.getLongitude();
        Double endLatitude = endAddress.getLatitude();
        Double endLongitude = endAddress.getLongitude();

        Double moveLatitude = Math.abs(startLatitude - endLatitude);;
        Double moveLongitude = Math.abs(startLongitude - endLongitude);
        if(startLatitude > endLatitude) moveLatitude = -moveLatitude;
        if(startLongitude > endLongitude) moveLongitude = -moveLongitude;

        for(int i = 0; i < END_TIME; i++) {
            DeliveryLocation deliveryLocation = new DeliveryLocation(startLatitude + moveLatitude / END_TIME * i, startLongitude + moveLongitude / END_TIME * i);
            for (SocketIOClient client : socketIOServer.getRoomOperations("user"+buyerId).getClients()) {
                client.sendEvent(NOTICE_DRONE_LOCATION.getEventName(), deliveryLocation);
            }
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.info("드론 배달 위치 전송 중 에러가 발생했습니다.");
            }
        }
        //TODO: 배달이 끝나고 배달 완료 메시지 반환
        order.setOrderStatus(OrderStatus.COMPLETE);
        orderRepositoryToSave.save(order);
        OrderNoticeResponse orderNoticeResponse = orderService.findOrderNotice(buyerId, orderId);
        for (SocketIOClient client : socketIOServer.getRoomOperations("user"+buyerId).getClients()) {
            client.sendEvent(NOTICE_ORDER_SELLER.getEventName(), orderNoticeResponse);
        }
        for (SocketIOClient client : socketIOServer.getRoomOperations("user"+sellerId).getClients()) {
            client.sendEvent(NOTICE_ORDER_SELLER.getEventName(), orderNoticeResponse);
        }
    }

    private Order findOrderByOrderId(Long orderId) {
        return orderRepository.findOrderByOrderId(orderId).orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private Buyer findBuyerByUserId(Long userId) {
        return buyerRepository.findBuyerByUserId(userId).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }


}

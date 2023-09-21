package com.supercoding.hanyipman.service;


import com.supercoding.hanyipman.dto.delivery.response.DeliveryLocation;
import com.supercoding.hanyipman.dto.vo.SendSseResponse;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.enums.EventName;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.AddressErrorCode;
import com.supercoding.hanyipman.error.domain.BuyerErrorCode;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;
import java.util.stream.IntStream;

import static com.supercoding.hanyipman.enums.EventName.NOTICE_DRON_LOCATION;


@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {
    private final BuyerRepository buyerRepository;
    private final SseMessageService sseMessageService;
    private final AddressRepository addressRepository;
    private final Integer END_TIME = 60;

    @Transactional(readOnly = true)
    public void getDeliveryLocation(Long userId, Long startAddrId, Long endAddrId) {
        Address startAddress = findAddressByAddressId(startAddrId);
        Address endAddress = findAddressByAddressId(endAddrId);
        findBuyerByUserId(userId);

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
            sseMessageService.sendSse(SendSseResponse.of(userId, deliveryLocation, NOTICE_DRON_LOCATION.getEventName()));
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.info("드론 배달 위치 전송 중 에러가 발생했습니다.");
            }
        }
    }

    private Address findAddressByAddressId(Long startAddrId) {
        return addressRepository.findById(startAddrId).orElseThrow(() -> new CustomException(AddressErrorCode.ADDRESS_NOT_FOUND));
    }

    private Buyer findBuyerByUserId(Long userId) {
        return buyerRepository.findBuyerByUserId(userId).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
    }


}

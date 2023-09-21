package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.delivery.request.RequestDeliveryAddress;
import com.supercoding.hanyipman.dto.delivery.response.DeliveryLocation;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.DeliveryService;
import com.supercoding.hanyipman.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @GetMapping(headers = "X-API-VERSION=1")
    public void getDeliveryLocation(@RequestParam("startAddrId") Long startAddrId,
                                           @RequestParam("endAddrId") Long endAddrId,
                                           @AuthenticationPrincipal CustomUserDetail auth) {
        deliveryService.getDeliveryLocation(auth.getUserId(), startAddrId, endAddrId);
    }
}

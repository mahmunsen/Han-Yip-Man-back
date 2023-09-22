package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.service.DeliveryService;
import lombok.RequiredArgsConstructor;
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
    public void getDeliveryLocation(@RequestParam("orderId") Long orderId,
                                           @AuthenticationPrincipal CustomUserDetail auth) {
        deliveryService.getDeliveryLocation(auth.getUserId(),auth.getUserId(), orderId);
    }
}

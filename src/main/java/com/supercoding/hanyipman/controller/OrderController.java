package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.order.request.RegisterOrderRequest;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.OrderService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="주문 관리")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    
    //TODO: 임시 테스트 url 실제는 api/payment url에서 주문과 결제가 이뤄짐
    @Operation(summary = "주문 등록", description = "장바구니에 담겨진 메뉴들을 주문함")
    @PostMapping(headers = "X-API-VERSION=1")
    public Response<Void> order(@RequestBody RegisterOrderRequest request,
                                @AuthenticationPrincipal CustomUserDetail auth) {
        orderService.order(auth.getUserId(), request.getAddressId(), request.getBuyerCouponId());

        return ApiUtils.success(HttpStatus.CREATED, "성공적으로 주문됐습니다.", null);
    }


}

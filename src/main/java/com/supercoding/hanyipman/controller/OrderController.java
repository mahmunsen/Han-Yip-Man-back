package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.order.request.RegisterOrderRequest;
import com.supercoding.hanyipman.dto.order.response.ViewOrderDetailResponse;
import com.supercoding.hanyipman.dto.order.response.ViewOrderResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.CustomPageable;
import com.supercoding.hanyipman.dto.vo.PageResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.service.OrderService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

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

    @Operation(summary = "주문목록 조회", description = "주문했던 목록을 가져옴")
    @GetMapping(headers = "X-API-VERSION=1")
    public Response<PageResponse<ViewOrderResponse>> getOrders(CustomPageable pageable,
                                                               @AuthenticationPrincipal CustomUserDetail auth) {
        PageResponse<ViewOrderResponse> orders = orderService.findOrders(auth.getUserId(), pageable);
        return ApiUtils.success(HttpStatus.OK, "성공적으로 주문내역을 조회했습니다.", orders);
    }


    /** 결제 성공 시 주문내역 조회하는 API */
    @TimeTrace
    @Operation(summary = "주문내역 조회", description = "결제 이후(결제 성공 시/결제 취소시) 주문내역 조회")
    @GetMapping(path = "/{order_id}", headers = "X-API-VERSION=1")
    public Response<Object> viewOrderDetail(@PathVariable("order_id") Long orderId) throws ParseException {
        ViewOrderDetailResponse viewOrderDetailResponse = orderService.viewOrderDetail(JwtToken.user(), orderId);
        return ApiUtils.success(HttpStatus.OK.value(), "주문 내역 조회에 성공하였습니다.", viewOrderDetailResponse);
    }
}

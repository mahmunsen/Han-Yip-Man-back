package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.orderTest.ViewOrderDetailResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.service.OrderTestService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/orderTests")
@Api(tags = "주문테스트 API")
public class OrderTestController {

    private final OrderTestService orderTestService;

    /* todo 결제 성공 시 주문내역 조회하는 API */
    @ApiOperation(value = "주문내역 조회 API ", nickname = "결제 성공 시 주문내역 조회하는 API")
    @GetMapping(path = "/{order_id}", headers = "X-API-VERSION=1")
    public Response<Object> viewOrderDetail(@PathVariable("order_id") Long orderId) throws ParseException {

        ViewOrderDetailResponse viewOrderDetailResponse = orderTestService.viewOrderDetail(JwtToken.user(), orderId);

        return ApiUtils.success(HttpStatus.OK.value(), "주문 내역 조회에 성공하였습니다.", viewOrderDetailResponse);
    }
}

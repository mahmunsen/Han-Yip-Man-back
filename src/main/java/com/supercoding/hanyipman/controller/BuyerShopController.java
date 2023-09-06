package com.supercoding.hanyipman.controller;


import com.supercoding.hanyipman.dto.shop.buyer.request.ViewShopListRequest;
import com.supercoding.hanyipman.dto.shop.buyer.response.ViewShopListResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.BuyerShopService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/buyer-shops")
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = "소비자 가게 관련 API")
public class BuyerShopController {

    private final BuyerShopService buyerShopService;

    @GetMapping(value="", headers="X-API-VERSION=1")
    public Response<ViewShopListResponse> findShopList(ViewShopListRequest viewShopListRequest,
                                                       @AuthenticationPrincipal CustomUserDetail customUserDetail) {

        return ApiUtils.success(HttpStatus.OK, "리스트 조회 성공", buyerShopService.findShopList(viewShopListRequest, customUserDetail));
    }

}

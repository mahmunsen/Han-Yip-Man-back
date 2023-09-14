package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterShopRequest;
import com.supercoding.hanyipman.dto.Shop.seller.response.ShopDetailResponse;
import com.supercoding.hanyipman.dto.Shop.seller.response.ShopManagementListResponse;
import com.supercoding.hanyipman.dto.Shop.seller.response.ShopOrderResponse;
import com.supercoding.hanyipman.dto.address.request.ShopAddressRequest;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.service.SellerShopService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/seller-shops")
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = "사장님 가게 관련 API")
public class SellerShopController {

    private final SellerShopService sellerShopService;

    @Operation(summary = "가게 등록", description = "가게 정보를 입력하여 가게 레코드를 생성합니다.")
    @PostMapping(value = "", consumes = "multipart/form-data", headers="X-API-VERSION=1")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data",
            schema = @Schema(implementation = MultipartFile.class)))
    public Response<Void> registerShop(@ModelAttribute RegisterShopRequest registerShopRequest,
                                        @ModelAttribute ShopAddressRequest shopAddressRequest,
                                        @ApiParam(value = "배너 이미지 파일 (선택)")
                                        @RequestPart(value = "bannerImage", required = false)
                                            MultipartFile bannerImage,
                                        @ApiParam(value = "썸네일 이미지 파일 (선택)")
                                        @RequestPart(value = "thumbnailImage", required = false)
                                            MultipartFile thumbnailImage) {

        sellerShopService.registerShop(registerShopRequest, shopAddressRequest, bannerImage, thumbnailImage, JwtToken.user());


        return ApiUtils.success(HttpStatus.CREATED, "가게 등록 성공", null);
    }

    @Operation(summary = "가게 삭제", description = "가게 아이디를 입력하여 가게 레코드를 제거합니다.")
    @DeleteMapping(value = "/shops/{shop_id}", headers = "X-API-VERSION=1")
    public Response<Void> deleteShop(@PathVariable(value = "shop_id") Long shopId) {

        sellerShopService.deleteShop(shopId, JwtToken.user());

        return ApiUtils.success(HttpStatus.OK, "가게 제거 완료", null);
    }

    @Operation(summary = "내 가게 리스트 조회", description = "토큰 정보로 로그인한 사장님이 관리중인 가게의 이름 레코드를 조회합니다")
    @GetMapping(value = "/shops", headers = "X-API-VERSION=1")
    public Response<List<ShopManagementListResponse>> findShopManagementList() {
        return ApiUtils.success(HttpStatus.OK, "관리 가게 조회 성공",  sellerShopService.findManagementList(JwtToken.user()));
    }

    @Operation(summary = "가게 상세 조회", description = "가게 상세 조회")
    @GetMapping(value = "/shops/{shop_id}", headers = "X-API-VERSION=1")
    public Response<ShopDetailResponse> findDetailShop(@PathVariable(value = "shop_id") Long shopId) {
        return ApiUtils.success(HttpStatus.OK, "가게 상세 조회 성공", sellerShopService.detailShop(shopId));
    }

    @Operation(summary = "가게 이름 중복 조회", description = "가게 이름을 요청하여 내가 관리중인 가게 중 중복 이름이 있는지 검사합니다.")
    @GetMapping(value = "/shops/duplication", headers = "X-API-VERSION=1")
    public Response<Void> checkDuplicationShopName(String shopName) {
        sellerShopService.checkDuplicationShopName(shopName);
        return ApiUtils.success(HttpStatus.OK, "내가 관리중인 가게중에 중복된 상호명이 없습니다.", null);
    }

    @TimeTrace
    @Operation(summary = "가게 주문 조회", description = "가게 주문 조회")
    @GetMapping(value = "/shops/{shop_id}/orders", headers = "X-API-VERSION=1")
    public Response<List<ShopOrderResponse>> findShopOrderList(@PathVariable(value = "shop_id") Long shopId) {
        return ApiUtils.success(HttpStatus.OK, "가게 주문 조회 성공", sellerShopService.findShopOrderList(shopId));
    }



}

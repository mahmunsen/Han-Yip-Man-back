package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.address.request.ShopAddressRequest;
import com.supercoding.hanyipman.dto.shop.seller.request.RegisterShopRequest;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.SellerShopService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/seller-shops")
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = "가게 관리")
public class SellerShopController {

    private final SellerShopService sellerShopService;

    @Operation(summary = "가게 등록", description = "가게 정보를 입력하여 가게 레코드를 생성합니다.")
    @PostMapping(value = "", consumes = "multipart/form-data")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = "multipart/form-data",
            schema = @Schema(implementation = MultipartFile.class)))
    public Response<Object> registerShop(@ModelAttribute RegisterShopRequest registerShopRequest,
                                        @ModelAttribute ShopAddressRequest shopAddressRequest,
                                        @ApiParam(value = "배너 이미지 파일 (선택)")
                                        @RequestPart(value = "bannerImage", required = false)
                                            MultipartFile bannerImage,
                                        @ApiParam(value = "썸네일 이미지 파일 (선택)")
                                        @RequestPart(value = "thumbnailImage", required = false)
                                            MultipartFile thumbnailImage,
                                        @AuthenticationPrincipal CustomUserDetail customUserDetail) {

        sellerShopService.registerShop(registerShopRequest, shopAddressRequest, bannerImage, thumbnailImage, customUserDetail);


        return ApiUtils.success(HttpStatus.CREATED, "가게 등록 성공", null);
    }

}

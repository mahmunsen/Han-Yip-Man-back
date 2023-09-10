package com.supercoding.hanyipman.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuGroup;
import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuRequest;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.MenuService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/seller-shops/menus")
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = "메뉴 관련 API")
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 등록", description = "대분류 정보를 입력하여 대분류 레코드를 생성합니다.")
    @PostMapping(value = "/{menu_group_id}",consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE}, headers = "X-API-VERSION=1")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(mediaType = "multipart/form-data",
            schema = @Schema(implementation = MultipartFile.class)),@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RegisterMenuRequest.class))})
    public Response<Void> createMenu(@RequestPart(name = "registerMenuRequest") RegisterMenuRequest registerMenuRequest,
                                     @RequestPart(required = false, name = "menuThumbnailImage") MultipartFile menuThumbnailImage,
                                     @PathVariable(value = "menu_group_id") Long menuGroupId) {
        menuService.createMenu(registerMenuRequest, menuThumbnailImage, menuGroupId);

        return ApiUtils.success(HttpStatus.CREATED, "메뉴 등록 성공", null);
    }

}

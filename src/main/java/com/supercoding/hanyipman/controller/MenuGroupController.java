package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.MenuGroupService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/seller-shops/menu-groups")
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = "사장님 가게 관련 API")
public class MenuGroupController {

    private final MenuGroupService menuGroupService;

    @Operation(summary = "대분류 등록", description = "대분류 정보를 입력하여 대분류 레코드를 생성합니다.")
    @PostMapping(value = "{shop_id}", headers = "X-API-VERSION=1")
    public Response<Void> createMenuGroup(@RequestBody String menuGroupName, @PathVariable(value = "shop_id") Long shopId) {
        menuGroupService.createMenuGroup(menuGroupName, shopId);
        return ApiUtils.success(HttpStatus.CREATED, menuGroupName + " 대분류 등록 성공", null);
    }

}

package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.Shop.seller.request.ChangeMenuGroupNameRequest;
import com.supercoding.hanyipman.dto.Shop.seller.request.ChangeMenuGroupRequest;
import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuGroup;
import com.supercoding.hanyipman.dto.Shop.seller.response.MenuGroupResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.MenuGroupService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/seller-shops/menu-groups")
@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = "사장님 가게 관련 API")
public class MenuGroupController {

    private final MenuGroupService menuGroupService;

    @Operation(summary = "대분류 등록", description = "대분류 정보를 입력하여 대분류 레코드를 생성합니다.")
    @PostMapping(value = "/{shop_id}", headers = "X-API-VERSION=1")
    public Response<Void> createMenuGroup(@RequestBody RegisterMenuGroup registerMenuGroup, @PathVariable(value = "shop_id") Long shopId) {
        menuGroupService.createMenuGroup(registerMenuGroup.getMenuGroupName(), shopId);
        return ApiUtils.success(HttpStatus.CREATED, registerMenuGroup.getMenuGroupName() + " 대분류 등록 성공", null);
    }

    @Operation(summary = "대분류 순서 수정", description = "대분류 리스트의 순서를 입력하여 대분류 레코드를 수정합니다.")
    @PatchMapping(value = "/{shop_id}", headers = "X-API-VERSION=1")
    public Response<Void> changeMenuGroupSequence(@PathVariable(value = "shop_id") Long shopId,
                                                  @RequestBody List<ChangeMenuGroupRequest> changeMenuGroupRequests) {
        menuGroupService.changeMenuGroupSequence(changeMenuGroupRequests, shopId);
        return ApiUtils.success(HttpStatus.OK, "대분류 순서 변경 성공", null);
    }

    @Operation(summary = "대분류 조회", description = "대분류 리스트 레코드를 조회합니다.")
    @GetMapping(value = "/{shop_id}", headers = "X-API-VERSION=1")
    public Response<List<MenuGroupResponse>> findMenuGroupList(@PathVariable(value = "shop_id") Long shopId) {
        return ApiUtils.success(HttpStatus.OK, "대분류 조회 성공", menuGroupService.findMenuGroupList(shopId));
    }

    @Operation(summary = "대분류 제거(hard_delete)", description = "대분류 리스트 레코드를 제거합니다.")
    @DeleteMapping(value = "/{shop_id}", headers = "X-API-VERSION=1")
    public Response<Void> findMenuGroupList(@PathVariable(value = "shop_id") Long shopId,
                                            @RequestParam Long menuGroupId) {
        menuGroupService.deleteMenuGroup(shopId, menuGroupId);
        return ApiUtils.success(HttpStatus.OK, "대분류 삭제 성공", null);
    }

    @Operation(summary = "대분류 이름 수정", description = "대분류 이름을 수정합니다")
    @PatchMapping(value = "{shop_id}/name", headers = "X-API-VERSION=1")
    public Response<Void> changeMenuGroupName(@PathVariable(value = "shop_id") Long shopId,
                                              @RequestBody ChangeMenuGroupNameRequest changeMenuGroupNameRequest) {
        menuGroupService.changeMenuGroupName(shopId, changeMenuGroupNameRequest);
        return ApiUtils.success(HttpStatus.OK, "대분류 이름 수정 성공", null);
    }
}

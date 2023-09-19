package com.supercoding.hanyipman.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuGroup;
import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterMenuRequest;
import com.supercoding.hanyipman.dto.Shop.seller.response.MenuResponse;
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

    @Operation(summary = "메뉴 등록", description = "메뉴 정보를 입력하여 메뉴 레코드를 생성합니다.")
    @PostMapping(value = "/{menu_group_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, headers = "X-API-VERSION=1")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {@Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = MultipartFile.class)), @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RegisterMenuRequest.class))})
    public Response<Void> createMenu(@RequestPart(name = "registerMenuRequest") RegisterMenuRequest registerMenuRequest,
                                     @RequestPart(required = false, name = "menuThumbnailImage")
                                     @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(type = "string", format = "binary")))
                                     MultipartFile menuThumbnailImage,
                                     @PathVariable(value = "menu_group_id") Long menuGroupId) {
        menuService.createMenu(registerMenuRequest, menuThumbnailImage, menuGroupId);

        return ApiUtils.success(HttpStatus.CREATED, "메뉴 등록 성공", null);
    }

    @Operation(summary = "메뉴 조회", description = "대분류 정보를 입력하여 메뉴 레코드를 조회합니다.")
    @GetMapping(value = "/{menu_group_id}", headers = "X-API-VERSION=1")
    public Response<List<MenuResponse>> findMenuList(@PathVariable(value = "menu_group_id") Long menuGroupId) {

        return ApiUtils.success(HttpStatus.OK, "메뉴 리스트 조회 성공", menuService.findMenuListByMenuGroupId(menuGroupId));
    }

    @Operation(summary = "메뉴 썸네일 수정", description = "메뉴 썸네일을 업로드하여 변경합니다")
    @PatchMapping(value = "/{menu_id}/thumbnail", headers = "X-API-VERSION=1")
    public Response<Void> changeMenuThumbnail(@PathVariable(value = "menu_id") Long menuId,
                                              @ApiParam(value = "배너 이미지 파일 (선택)")
                                              @RequestPart(value = "bannerImage", required = false)
                                              MultipartFile thumbnailImage) {
        menuService.changeThumbnail(thumbnailImage, menuId);
        return ApiUtils.success(HttpStatus.OK, "가게 썸네일 변경 성공", null);
    }

    @Operation(summary = "메뉴 이름 수정", description = "변경할 메뉴 이름을 요청하여 변경합니다")
    @PatchMapping(value = "/{menu_id}/name", headers = "X-API-VERSION=1")
    public Response<Void> changeMenuName(@PathVariable(value = "menu_id") Long menuId,
                                         @RequestParam String name) {
        menuService.changeName(name, menuId);
        return ApiUtils.success(HttpStatus.OK, "메뉴 이름 변경 성공", null);
    }

    @Operation(summary = "메뉴 가격 수정", description = "변경할 메뉴 가격을 요청하여 변경합니다")
    @PatchMapping(value = "/{menu_id}/price", headers = "X-API-VERSION=1")
    public Response<Void> changeMenuPrice(@PathVariable(value = "menu_id") Long menuId,
                                          @RequestParam Integer price) {
        menuService.changePrice(price, menuId);
        return ApiUtils.success(HttpStatus.OK, "메뉴 가격 변경 성공", null);
    }

    @Operation(summary = "메뉴 설명 수정", description = "메뉴 설명을 요청하여 변경합니다")
    @PatchMapping(value = "/{menu_id}/description", headers = "X-API-VERSION=1")
    public Response<Void> changeMenuDescription(@PathVariable(value = "menu_id") Long menuId,
                                                @RequestParam String description) {
        menuService.changeDescription(description, menuId);
        return ApiUtils.success(HttpStatus.OK, "메뉴 설명 변경 성공", null);
    }

    @Operation(summary = "메뉴가 속한 대분류 변경", description = "메뉴가 속한 대분류를 변경합니다")
    @PatchMapping(value = "/{menu_id}/menuGroup", headers = "X-API-VERSION=1")
    public Response<Void> changeMenuMenuGroup(@PathVariable(value = "menu_id") Long menuId,
                                              @RequestParam Long menuGroupId) {
        menuService.changeMenuGroup(menuGroupId, menuId);
        return ApiUtils.success(HttpStatus.OK, "메뉴가 속한 대분류 변경 성공", null);
    }

    @Operation(summary = "메뉴 제거", description = "메뉴와 하위 옵션등을 제거합니다")
    @DeleteMapping(value = "/{menu_id}", headers = "X-API-VERSION=1")
    public Response<Void> deleteMenu(@PathVariable(value = "menu_id") Long menuId) {
        menuService.deleteMenu(menuId);
        return ApiUtils.success(HttpStatus.OK, "메뉴 삭제 성공", null);
    }
}

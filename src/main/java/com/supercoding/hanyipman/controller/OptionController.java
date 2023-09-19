package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.option.request.RegisterOptionItemRequest;
import com.supercoding.hanyipman.dto.option.request.RegisterOptionRequest;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.OptionService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller-shops/options")
@Slf4j
@Api(tags = "옵션 관련 API")
public class OptionController {

    private final OptionService optionService;

    @Operation(summary = "옵션 신규 등록", description = "옵션 정보를 입력하여 옵션 레코드를 생성합니다.")
    @PostMapping(value = "/menus/{menu_id}", headers = "X-API-VERSION=1")
    public Response<Void> registerOption(@PathVariable(value = "menu_id") Long menuId,
                                         @RequestBody RegisterOptionRequest registerOptionRequest) {
        optionService.registerOption(registerOptionRequest, menuId);
        return ApiUtils.success(HttpStatus.CREATED, "옵션 등록 성공", null);
    }

    @Operation(summary = "옵션 수정", description = "옵션 정보를 입력하여 옵션 레코드를 수정합니다.")
    @PutMapping(value = "/{option_id}", headers = "X-API-VERSION=1")
    public Response<Void> changeOptionName(@PathVariable(value = "option_id") Long optionId,
                                           @RequestBody RegisterOptionRequest registerOptionRequest) {
        optionService.changeOption(registerOptionRequest, optionId);
        return ApiUtils.success(HttpStatus.OK, "옵션 수정 성공", null);
    }

    @Operation(summary = "옵션 아이템 신규 등록", description = "옵션 정보를 입력하여 옵션 레코드를 생성합니다.")
    @PostMapping(value = "/{option_id}", headers = "X-API-VERSION=1")
    public Response<Void> registerOptionItem(@PathVariable(value = "option_id") Long optionId,
                                             @RequestBody RegisterOptionItemRequest registerOptionItemRequest) {
        optionService.registerOptionItem(registerOptionItemRequest, optionId);
        return ApiUtils.success(HttpStatus.CREATED, "옵션 아이템 등록 성공", null);
    }

    @Operation(summary = "옵션 아이템 수정", description = "옵션 아이템 정보를 입력하여 옵션 아이템 레코드를 수정합니다.")
    @PutMapping(value = "option-items/{option_item_id}", headers = "X-API-VERSION=1")
    public Response<Void> changeOptionItem(@PathVariable("option_item_id") Long optionItemId,
                                           @RequestBody RegisterOptionItemRequest registerOptionItemRequest) {
        optionService.changeOptionItem(registerOptionItemRequest, optionItemId);
        return ApiUtils.success(HttpStatus.OK, "옵션 아이템 수정 성공", null);
    }

    @Operation(summary = "옵션 아이템이 속한 옵션 변경", description = "새로 속할 옵션과 변경할 옵션 아이템의 정보를 입력하여 옵션 아이템이 속한 옵션을 변경합니다.")
    @PatchMapping(value = "option-items/{option_item_id}", headers = "X-API-VERSION=1")
    public Response<Void> changeOptionItemByOption(@PathVariable("option_item_id") Long optionItemId,
                                                   @RequestParam Long optionId) {
        optionService.changeOptionItemByOption(optionItemId, optionId);
        return ApiUtils.success(HttpStatus.OK, "옵션 아이템 수정 성공", null);
    }

    @Operation(summary = "옵션 제거", description = "옵션과 하위 옵션 아이템들을 제거합니다")
    @DeleteMapping(value = "/{option_id}", headers = "X-API-VERSION=1")
    public Response<Void> deleteOption(@PathVariable("option_id") Long optionId) {
        optionService.deleteOption(optionId);
        return ApiUtils.success(HttpStatus.OK, "옵션 삭제 성공", null);
    }

    @Operation(summary = "옵션 아이템 제거", description = "옵션 아이템을 제거합니다")
    @DeleteMapping(value = "/option-items/{option_item_value}", headers = "X-API-VERSION=1")
    public Response<Void> deleteOptionItem(@PathVariable(value = "option_item_value")Long optionItemValue) {
        optionService.deleteOptionItem(optionItemValue);
        return ApiUtils.success(HttpStatus.OK, "옵션 아이템 삭제 성공", null);
    }
}

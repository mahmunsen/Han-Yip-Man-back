package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.address.request.AddressRegisterRequest;
import com.supercoding.hanyipman.dto.address.response.AddressListResponse;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.security.JwtToken;
import com.supercoding.hanyipman.service.AddressService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "회원 주소")
@RequestMapping("/api/addresses")
public class AddressController {
    private final AddressService addressService;

    // 주소 등록
    @PostMapping(value = "/register", headers = "X-API-VERSION=1")
    @Operation(summary = "주소 등록", description = "위도경도 등등 주소관련 데이터를 등록합니다.")
    public Response<Void> registerAddress(AddressRegisterRequest addressRegisterRequest) {
        String saveAddress = addressService.registerAddress(JwtToken.user().validBuyer(), addressRegisterRequest);
        return ApiUtils.success(HttpStatus.CREATED, saveAddress + " 주소가 추가되었습니다.", null);
    }

    // 주소 목록 조회
    @GetMapping(headers = "X-API-VERSION=1")
    @Operation(summary = "주소 조회", description = "나의 등록된 주소 모두를 불러옵니다.")
    public Response<List<AddressListResponse>> getAddressList() {
        return ApiUtils.success(HttpStatus.OK, JwtToken.user().getEmail() + " 유저의 등록된 주소입니다.", addressService.getAddressList(JwtToken.user().validBuyer()));
    }

    // 주소 수정
    @Operation(summary = "주소 수정", description = "해당 주소Id의 주소 정보를 수정합니다.")
    @PatchMapping(value = "/{address_id}", headers = "X-API-VERSION=1")
    public Response<Void> patchAddress(@ApiParam(value = "수정 할 주소Id 입력 필드", type = "Long", name = "address_id") @PathVariable("address_id") Long addressId, AddressRegisterRequest addressRegisterRequest) {
        Long patchId = addressService.patchAddress(JwtToken.user().validBuyer(), addressId, addressRegisterRequest);
        return ApiUtils.success(HttpStatus.NO_CONTENT, patchId + "번 주소가 수정되었습니다.", null);
    }

    // 주소 삭제
    @Operation(summary = "주소 삭제", description = "주소Id에 맞는 주소데이터를 삭제합니다.")
    @DeleteMapping(value = "/{address_id}", headers = "X-API-VERSION=1")
    public Response<Void> sellerDeleteAddress(
            @ApiParam(value = "삭제 할 주소Id 입력 필드", type = "Long", name = "address_id") @PathVariable("address_id") Long addressId) {
        String deleteAddress = addressService.sellerDeleteAddress(JwtToken.user().getBuyer(), addressId);
        return ApiUtils.success(HttpStatus.NO_CONTENT, deleteAddress + " 주소 삭제 완료", null);
    }
    // 기본 주소 등록
    @Operation(summary = "기본 주소 설정", description = "기본 배송지로 선택할 주소를 선택합니다.")
    @PostMapping(value = "/set-default-address", headers = "X-API-VERSION=1")
    @ApiImplicitParams({@ApiImplicitParam(name = "defaultAddressId", value = "기본 배송지로 등록 할 주소Id 입력 필드", required = true)})
    public Response<Void> setDefaultAddress(Long defaultAddressId) {
        addressService.setDefaultAddress(JwtToken.user().getBuyer(), defaultAddressId);
        return ApiUtils.success(HttpStatus.OK, "기본 주소가 변경되었습니다.", null);
    }

    @Operation(summary = "주소 중복 확인", description = "내 배송지 리스트를 조회해 중복 검사를 시행합니다")
    @GetMapping(value = "/duplication", headers = "X-API-VERSION=1")
    public Response<Boolean> checkDuplicationAddress(String mapId) {
        return ApiUtils.success(HttpStatus.OK, "카카오 mapId 중복 체크 검사 결과", addressService.checkDuplicationAddress(mapId));
    }
}


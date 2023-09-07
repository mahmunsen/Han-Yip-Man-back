package com.supercoding.hanyipman.controller;

import com.supercoding.hanyipman.dto.cart.request.RegisterCartRequest;
import com.supercoding.hanyipman.dto.cart.request.UpdateCartRequest;
import com.supercoding.hanyipman.dto.cart.response.ViewCartResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.dto.vo.CustomPageable;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.service.CartService;
import com.supercoding.hanyipman.utils.ApiUtils;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@Api(tags="장바구니 관리")
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니 등록", description = "사용자가 메뉴, 옵션을 담은 장바구니를 서버에 저장한다. 옵션에서 \"\"를 제거하고 실행해주세요")
    @PostMapping
    public Response<Void> registerCart(@RequestBody RegisterCartRequest request,
                               @AuthenticationPrincipal CustomUserDetail userDetail){
        cartService.registerCart(
                userDetail.getUserId(),
                request.getShopId(),
                request.getMenuId(),
                request.getOptions(),
                request.getAmount()
        );
        return ApiUtils.success(HttpStatus.CREATED, "장바구니에 성공적으로 담았습니다.", null);
    }

//    @Operation(summary = "장바구니 조회", description = "사용자가 담았던 장바구니 리스트를 반환한다.")
//    @GetMapping
//    public Response<?> viewAllCartsV1(Pageable pageable){
//        Page<ViewCartResponse> cartsResponse =  cartService.findUnpaidCartsV1(pageable);
//        return ApiUtils.success(HttpStatus.OK, "장바구니를 성공적으로 가져왔습니다.", cartsResponse);
//    }

    @Operation(summary = "장바구니 조회", description = "사용자가 담았던 장바구니 리스트를 반환한다.")
    @GetMapping
    public Response<List<ViewCartResponse>> viewAllCartsV2(CustomPageable pageable){
        List<ViewCartResponse> cartsResponse =  cartService.findUnpaidCartsAndOptionItemsV2(pageable);
        return ApiUtils.success(HttpStatus.OK, "장바구니를 성공적으로 가져왔습니다.", cartsResponse);
    }

    @Operation(summary = "장바구니 수정", description = "기존 장바구니의 수량을 변경한다.")
    @PatchMapping
    public Response<Void> updateCart(@RequestBody UpdateCartRequest request,
                                  @AuthenticationPrincipal CustomUserDetail auth){
        cartService.updateCart(request.getCartId(), request.getAmount(), auth.getUserId());
        return ApiUtils.success(HttpStatus.OK, "장바구니 수량을 성공적으로 변경했습니다.", null);
    }

    @Operation(summary = "장바구니 단건 삭제", description = "기존에 장바구니를 삭제한다.")
    @DeleteMapping("/{cart_id}")
    public Response<Void> updateCart(@PathVariable("cart_id") Long cartId,
                                     @AuthenticationPrincipal CustomUserDetail auth){
        cartService.deleteCart(auth.getUserId(), cartId);
        return ApiUtils.success(HttpStatus.OK, "장바구니 제거했습니다.", null);
    }


}

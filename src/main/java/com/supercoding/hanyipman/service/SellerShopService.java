package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterShopRequest;
import com.supercoding.hanyipman.dto.Shop.seller.response.ShopDetailResponse;
import com.supercoding.hanyipman.dto.Shop.seller.response.ShopManagementListResponse;
import com.supercoding.hanyipman.dto.Shop.seller.response.ShopOrderResponse;
import com.supercoding.hanyipman.dto.Shop.seller.response.ShopOrderCategorizedResponse;
import com.supercoding.hanyipman.dto.address.request.ShopAddressRequest;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.enums.FilePath;
import com.supercoding.hanyipman.enums.OrderStatus;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.FileErrorCode;
import com.supercoding.hanyipman.error.domain.SellerErrorCode;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.repository.CategoryRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.repository.order.OrderRepository;
import com.supercoding.hanyipman.repository.shop.ShopCustomRepositoryImpl;
import com.supercoding.hanyipman.repository.shop.ShopRepository;
import com.supercoding.hanyipman.security.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerShopService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final ShopCustomRepositoryImpl shopCustomRepository;
    private final AwsS3Service awsS3Service;
    private final OrderRepository orderRepository;

    @Transactional
    public void registerShop(RegisterShopRequest registerShopRequest, ShopAddressRequest shopAddressRequest, MultipartFile bannerFile, MultipartFile thumbnailFile, User user) {

        Seller seller = validSellerUser(user);
        validationShopName(registerShopRequest.getShopName(), seller.getId());
        Category category = categoryRepository.findById(registerShopRequest.getCategoryId()).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_CATEGORY));

        Shop shop = Shop.from(registerShopRequest, seller, category);

        shopRepository.save(shop);
        shop.setBanner(uploadImageFile(bannerFile, shop));
        shop.setThumbnail(uploadImageFile(thumbnailFile, shop));
        shop.setAddress(Address.from(shopAddressRequest));
    }

    public void checkDuplicationShopName(String shopName) {
        Seller seller = validSellerUser(JwtToken.user());
        validationShopName(shopName, seller.getId());
    }

    public void deleteShop(Long shopId, User user) {

        Seller seller = validSellerUser(user);
        Shop shop = validShop(shopId, seller.getId());
        shopRepository.deleteById(shop.getId());
    }


    public List<ShopManagementListResponse> findManagementList(User user) {
        Seller seller = validSellerUser(user);

        List<Shop> shopList = shopRepository.findAllBySellerAndIsDeletedFalse(seller);

        return shopList.stream().map(ShopManagementListResponse::from).collect(Collectors.toList());
    }

    public ShopDetailResponse detailShop(Long shopId) {

        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        return ShopDetailResponse.from(shop);
    }
    @Transactional
    public ShopOrderCategorizedResponse findShopOrderList(Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        List<OrderStatus> orderStatusesToFind = Arrays.asList(OrderStatus.PAID, OrderStatus.TAKEOVER, OrderStatus.DELIVERY, OrderStatus.COOKING);
        List<Order> orders = orderRepository.findOrderExceptCompleted(shop, orderStatusesToFind).orElse(null);
        List<ShopOrderResponse> shopOrderResponses = orders.stream().map(order -> ShopOrderResponse.from(order)).collect(Collectors.toList());

        return shopOrderCategorize(shopOrderResponses);
    }

    public void changeThumbnail(MultipartFile thumbnailImage, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());

        String newThumbnailUrl = updateImageFile(thumbnailImage, shop, shop.getThumbnail());
        shop.setThumbnail(newThumbnailUrl);
        shopRepository.save(shop);
    }

    public void changeBanner(MultipartFile bannerImage, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());

        String newThumbnailUrl = updateImageFile(bannerImage, shop, shop.getBanner());
        shop.setBanner(newThumbnailUrl);
        shopRepository.save(shop);
    }

    public void changeShopName(String shopName, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        validationShopName(shopName, seller.getId());
        shop.setName(shopName);
        shopRepository.save(shop);
    }

    public void changeCategory(Long categoryId, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_CATEGORY));
        shop.setCategory(category);
        shopRepository.save(shop);
    }

    public void changePhoneNum(String phoneNumber, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        shop.setPhoneNum(phoneNumber);
        shopRepository.save(shop);
    }

    public void changeMinOrderPrice(Integer minOrderPrice, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        shop.setMinOrderPrice(minOrderPrice);
        shopRepository.save(shop);
    }

    public void changeDescription(String description, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        shop.setDescription(description);
        shopRepository.save(shop);
    }

    public void changeBusinessNumber(String businessNumber, Long shopId) {
        Seller seller = validSellerUser(JwtToken.user());
        Shop shop = validShop(shopId, seller.getId());
        shop.setBusinessNumber(businessNumber);
        shopRepository.save(shop);
    }

    private String uploadImageFile(MultipartFile multipartFile, Shop shop) {
        String uniqueIdentifier = UUID.randomUUID().toString();
        try {
            if (multipartFile != null) {
                return awsS3Service.uploadImage(multipartFile, FilePath.SHOP_DIR.getPath() + shop.getId() + "/" + uniqueIdentifier);
            }
        }catch (IOException e) {
            throw new CustomException(FileErrorCode.FILE_UPLOAD_FAILED);
        }
        return null;
    }

    private String updateImageFile(MultipartFile multipartFile, Shop shop, String originUrl) {
        String uniqueIdentifier = UUID.randomUUID().toString();
        try {
            if (multipartFile != null) {
                awsS3Service.removeFile(originUrl);
                return awsS3Service.uploadImage(multipartFile, FilePath.SHOP_DIR.getPath() + shop.getId() + "/" + uniqueIdentifier);
            }
        }catch (IOException e) {
            throw new CustomException(FileErrorCode.FILE_UPLOAD_FAILED);
        }
        return null;
    }

    private Seller validSellerUser(User user) {
        return sellerRepository.findByUser(user).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
    }

    private Shop validShop(Long shopId, Long sellerId) {
        Shop shop = shopRepository.findShopByShopId(shopId).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_SHOP));
        if (!Objects.equals(shop.getSeller().getId(), sellerId)) throw new CustomException(ShopErrorCode.DIFFERENT_SELLER);
        return shop;
    }

    private void validationShopName(String shopName, Long sellerId) {
        if (Boolean.TRUE.equals(shopCustomRepository.existShopNameBySeller(shopName, sellerId))) throw new CustomException(ShopErrorCode.DUPLICATION_SHOP_NAME);
    }

    private ShopOrderCategorizedResponse shopOrderCategorize(List<ShopOrderResponse> shopOrderResponses) {
        List<ShopOrderResponse> paidOrderList = shopOrderResponses.stream().filter(shopOrderResponse -> OrderStatus.valueOf(shopOrderResponse.getOrderStatus()).equals(OrderStatus.PAID)).collect(Collectors.toList());
        List<ShopOrderResponse> takeoverOrderList = shopOrderResponses.stream().filter(shopOrderResponse -> OrderStatus.valueOf(shopOrderResponse.getOrderStatus()).equals(OrderStatus.TAKEOVER)).collect(Collectors.toList());
        List<ShopOrderResponse> deliveryOrderList = shopOrderResponses.stream().filter(shopOrderResponse -> OrderStatus.valueOf(shopOrderResponse.getOrderStatus()).equals(OrderStatus.DELIVERY)).collect(Collectors.toList());
        List<ShopOrderResponse> cookingOrderList = shopOrderResponses.stream().filter(shopOrderResponse -> OrderStatus.valueOf(shopOrderResponse.getOrderStatus()).equals(OrderStatus.COOKING)).collect(Collectors.toList());

        return new ShopOrderCategorizedResponse(paidOrderList, takeoverOrderList, cookingOrderList, deliveryOrderList);

    }


}

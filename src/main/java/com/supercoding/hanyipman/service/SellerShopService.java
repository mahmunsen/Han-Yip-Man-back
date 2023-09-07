package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.address.request.ShopAddressRequest;
import com.supercoding.hanyipman.dto.shop.seller.request.RegisterShopRequest;
import com.supercoding.hanyipman.dto.shop.seller.response.ShopManagementListResponse;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.*;
import com.supercoding.hanyipman.repository.CategoryRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.Shop.ShopRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.enums.FilePath;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellerShopService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public void registerShop(RegisterShopRequest registerShopRequest, ShopAddressRequest shopAddressRequest, MultipartFile bannerFile, MultipartFile thumbnailFile, User user) {

        Seller seller = validSellerUser(user);
        Category category = categoryRepository.findById(registerShopRequest.getCategoryId()).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_CATEGORY));
        Shop shop = Shop.from(registerShopRequest, seller, category);

        shopRepository.save(shop);
        shop.setBanner(uploadImageFile(bannerFile, shop));
        shop.setThumbnail(uploadImageFile(thumbnailFile, shop));
        shop.setAddress(Address.from(shopAddressRequest));

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

    public List<ShopManagementListResponse> findManagementList(User user) {
        Seller seller = validSellerUser(user);

        List<Shop> shopList = shopRepository.findAllBySeller(seller);

        return shopList.stream().map(ShopManagementListResponse::from).collect(Collectors.toList());
    }

    private Seller validSellerUser(User user) {
        return sellerRepository.findByUser(user).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
    }

    private CustomUserDetail validUserDetail(CustomUserDetail customUserDetail) {
        return Optional.ofNullable(customUserDetail).orElseThrow(() -> new CustomException(TokenErrorCode.ACCESS_DENIED));
    }

}

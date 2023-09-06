package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.address.request.ShopAddressRequest;
import com.supercoding.hanyipman.dto.shop.seller.request.RegisterShopRequest;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.FileErrorCode;
import com.supercoding.hanyipman.error.domain.SellerErrorCode;
import com.supercoding.hanyipman.error.domain.ShopErrorCode;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerShopService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public void registerShop(RegisterShopRequest registerShopRequest, ShopAddressRequest shopAddressRequest, MultipartFile bannerFile, MultipartFile thumbnailFile, CustomUserDetail customUserDetail) {

        Seller seller = validSellerUser(customUserDetail);
        Category category = categoryRepository.findById(registerShopRequest.getCategoryId()).orElseThrow(() -> new CustomException(ShopErrorCode.NOT_FOUND_CATEGORY));
        Shop shop = Shop.from(registerShopRequest, seller, category);

        shopRepository.save(shop);
        shop.setBanner(uploadImageFile(bannerFile, shop));
        shop.setThumbnail(uploadImageFile(thumbnailFile, shop));
        shop.setAddress(Address.from(shopAddressRequest));

    }

    private Seller validSellerUser(CustomUserDetail customUserDetail) {
        User validUser = userRepository.findById(customUserDetail.getUserId()).orElseThrow(() -> new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
        return sellerRepository.findByUser(validUser).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
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

}

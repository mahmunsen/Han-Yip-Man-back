package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.reivew.request.RegisterReviewRequest;
import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.Review;
import com.supercoding.hanyipman.entity.Shop;
import com.supercoding.hanyipman.enums.FilePath;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.FileErrorCode;
import com.supercoding.hanyipman.error.domain.ReviewErrorCode;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.ReviewRepository;
import com.supercoding.hanyipman.repository.Shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final BuyerRepository buyerRepository;
    private final ReviewRepository reviewRepository;
    private final ShopRepository shopRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public void registerReview(CustomUserDetail userDetail, RegisterReviewRequest registerReviewRequest) {
        Long userId = userDetail.getUserId();
        Buyer buyer = validateUser(userId);

        Shop shop = shopRepository.findById(registerReviewRequest.getShopId()).orElseThrow(()->new CustomException(ReviewErrorCode.STORE_NOT_FOUND));

        if(reviewRepository.existsByBuyerAndShop(buyer, shop)) throw new CustomException(ReviewErrorCode.REGISTERED_BEFORE);

        Review reviewToRegister = Review.builder()
                .shop(shop)
                .buyer(buyer)
                .content(registerReviewRequest.getReviewContent())
                .score(registerReviewRequest.getReviewScore())
                .build();
        reviewRepository.save(reviewToRegister);

        if (registerReviewRequest.getReviewImage() != null) {
            reviewToRegister.setImageUrl(uploadImageFile(registerReviewRequest.getReviewImage(), reviewToRegister));
        }

    }

    private Buyer validateUser(Long userId) {
        return buyerRepository.findBuyerByUserId(userId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_PROPER_USER));
    }

    private String uploadImageFile(MultipartFile multipartFile, Review review) {
        try {
            if (multipartFile != null) {
                return awsS3Service.uploadImage(multipartFile, FilePath.REVIEW_DIR.getPath() + review.getId());
            }
        }catch (IOException e) {
            throw new CustomException(FileErrorCode.FILE_UPLOAD_FAILED);
        }
        return null;
    }

}

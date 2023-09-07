package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.reivew.request.RegisterReviewRequest;
import com.supercoding.hanyipman.dto.reivew.request.ViewShopReviewsRequest;
import com.supercoding.hanyipman.dto.reivew.response.ShopReview;
import com.supercoding.hanyipman.dto.reivew.response.ViewShopReviewsResponse;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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

        Shop shop = validateShop(registerReviewRequest.getShopId());

        if (reviewRepository.existsByBuyerAndShop(buyer, shop))
            throw new CustomException(ReviewErrorCode.REGISTERED_BEFORE);

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

    public ViewShopReviewsResponse viewShopReviews(String shopId, ViewShopReviewsRequest viewShopReviewsRequest) {

        Shop shop = validateShop(Long.valueOf(shopId));
        Integer score = viewShopReviewsRequest.getReviewScore();
        Instant cursor = viewShopReviewsRequest.getCursor();


        Pageable pageable = PageRequest.of(0, viewShopReviewsRequest.getSize(), Sort.Direction.DESC, "createdAt");
        List<ShopReview> shopReviewList;
        if (score == null) {
           shopReviewList = getShopReviewListWhenScoreNull(shop, cursor ,pageable);
        }else{
            shopReviewList = getShopReviewListWithScore(shop, score, cursor, pageable);
        }

        Instant returnCursor = shopReviewList.isEmpty() ? null : shopReviewList.get(shopReviewList.size() - 1).getCreatedAt();

        return ViewShopReviewsResponse.builder()
                .cursor(returnCursor)
                .shopReviewsList(shopReviewList)
                .build();
    }

    public Double viewShopReviewAverage(String shopId) {
        Double reviewScoreAverage = reviewRepository.findAll().stream().map(review -> review.getScore()).mapToInt(Integer::intValue).average().orElse(0.0);
        return Math.round(reviewScoreAverage)+0.5;
    }

    private Buyer validateUser(Long userId) {
        return buyerRepository.findBuyerByUserId(userId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_PROPER_USER));
    }

    private Shop validateShop(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.STORE_NOT_FOUND));
    }

    private String uploadImageFile(MultipartFile multipartFile, Review review) {
        try {
            if (multipartFile != null) {
                return awsS3Service.uploadImage(multipartFile, FilePath.REVIEW_DIR.getPath() + review.getId());
            }
        } catch (IOException e) {
            throw new CustomException(FileErrorCode.FILE_UPLOAD_FAILED);
        }
        return null;
    }

    private List<ShopReview> getShopReviewListWhenScoreNull(Shop shop, Instant cursor, Pageable pageable) {
        List<ShopReview> shopReviewList;
        if (cursor == null) {
            shopReviewList = reviewRepository.findAllByShopOrderByCreatedAtDesc(shop, pageable).getContent().stream()
                    .map(review -> Review.toShopReview(review)).collect(Collectors.toList());
            return shopReviewList;
        }

        shopReviewList = reviewRepository.findAllByShopAndCreatedAtLessThanOrderByCreatedAtDesc(shop, cursor, pageable).stream()
                .map(review -> Review.toShopReview(review)).collect(Collectors.toList());
        return shopReviewList;
    }

    private List<ShopReview> getShopReviewListWithScore(Shop shop, Integer score, Instant cursor,  Pageable pageable) {
        List<ShopReview> shopReviewList;
        if (cursor == null) {
            shopReviewList = reviewRepository.findAllByShopAndScoreOrderByCreatedAtDesc(shop, score, pageable).getContent().stream()
                    .map(review -> Review.toShopReview(review)).collect(Collectors.toList());
            return shopReviewList;
        }

        shopReviewList = reviewRepository.findAllByShopAndScoreAndCreatedAtLessThanOrderByCreatedAtDesc(shop, score, cursor, pageable).stream()
                .map(review -> Review.toShopReview(review)).collect(Collectors.toList());
        return shopReviewList;
    }

}

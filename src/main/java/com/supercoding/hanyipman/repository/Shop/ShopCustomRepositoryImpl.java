package com.supercoding.hanyipman.repository.Shop;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.shop.buyer.request.ViewShopListRequest;
import com.supercoding.hanyipman.dto.shop.buyer.response.ShopList;
import com.supercoding.hanyipman.dto.shop.buyer.response.ViewShopListResponse;
import com.supercoding.hanyipman.enums.ShopSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static com.supercoding.hanyipman.entity.QShop.shop;
import static com.supercoding.hanyipman.entity.QReview.review;
import static com.supercoding.hanyipman.entity.QCategory.category;

@Repository
@RequiredArgsConstructor
public class ShopCustomRepositoryImpl implements ShopCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @TimeTrace
    @Override
    public ViewShopListResponse findShopListWithNextCursor(ViewShopListRequest request, Double latitude, Double longitude) {

        BooleanBuilder whereConditions = new BooleanBuilder();
        Long cursor = request.getCursor();
        if (cursor != null) {
            whereConditions.and(shop.id.lt(cursor));
        }

        Long categoryId = request.getCategoryId();
        if (categoryId != null && categoryId > 0) {
            whereConditions.and(shop.category.id.eq(categoryId));
        }

        NumberExpression<Double> distance = numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), point({2}, {3}))",
                longitude, latitude, shop.address.longitude, shop.address.latitude);
        whereConditions.and(distance.round().intValue().lt(1000));

        String searchKeyword = request.getSearchKeyword();
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            whereConditions.and(shop.name.contains(searchKeyword));
        }
        whereConditions.and(shop.isDeleted.eq(false));

        ShopSortType shopSortType = ShopSortType.fromString(request.getSortType());
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        switch (shopSortType) {
            case DISTANCE:
                orderSpecifiers.add(distance.asc());
                break;
            case AVG_RATING:
                orderSpecifiers.add(new CaseBuilder()
                        .when(review.in(shop.reviews)).then(review.score.avg()) // 리뷰가 있는 경우 평균 별점으로 정렬
                        .otherwise(0.0) // 리뷰가 없는 경우 0.0으로 정렬
                        .desc());
                break;
            case COUNT_REVIEW:
                orderSpecifiers.add(new CaseBuilder()
                        .when(review.in(shop.reviews)).then(review.count())
                        .otherwise(0L)
                        .desc());
                break;
            default:
                // 기본 정렬 설정: 거리, 평균 별점이 같은 경우 최신 순으로
                break;
        }

        orderSpecifiers.add(shop.id.desc()); //최신 순으로 추가 정렬

        List<ShopList> result = jpaQueryFactory
                                    .select(Projections.constructor(ShopList.class,
                                            shop.id,
                                            shop.name,
                                            shop.thumbnail,
                                            shop.description,
                                            shop.minOrderPrice,
                                            JPAExpressions
                                                    .select(review.score.avg())
                                                    .from(review)
                                                    .where(review.in(shop.reviews)),
                                            JPAExpressions
                                                    .select(review.count())
                                                    .from(review)
                                                    .where(review.in(shop.reviews)),
                                            shop.minOrderPrice,
                                            shop.defaultDeliveryPrice,
                                            distance.round().intValue()
                                                    ))
                .from(shop)
                .where(whereConditions)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .limit(request.getSize())
                .fetch();

        Long nextCursor = result.isEmpty() ? null : result.get(result.size() - 1).getShopId();

        return new ViewShopListResponse(result, nextCursor);
    }
}

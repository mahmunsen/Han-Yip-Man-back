package com.supercoding.hanyipman.repository.shop;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.supercoding.hanyipman.advice.annotation.TimeTrace;
import com.supercoding.hanyipman.dto.Shop.buyer.request.ViewShopListRequest;
import com.supercoding.hanyipman.dto.Shop.buyer.response.ShopList;
import com.supercoding.hanyipman.dto.Shop.buyer.response.ViewShopListResponse;
import com.supercoding.hanyipman.enums.ShopSortType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static com.supercoding.hanyipman.entity.QShop.shop;
import static com.supercoding.hanyipman.entity.QReview.review;
import static com.supercoding.hanyipman.entity.QMenuGroup.menuGroup;
import static com.supercoding.hanyipman.entity.QMenu.menu;
import static com.supercoding.hanyipman.entity.QOption.option;
import static com.supercoding.hanyipman.entity.QOptionItem.optionItem;
import static org.hibernate.internal.util.NullnessHelper.coalesce;

@Repository
@RequiredArgsConstructor
public class ShopCustomRepositoryImpl implements ShopCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @TimeTrace
    @Override
    public ViewShopListResponse findShopListWithNextCursor(ViewShopListRequest request, Double latitude, Double longitude) {


        NumberExpression<Double> distance = calcDistance(latitude, longitude);

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
                orderSpecifiers.add(review.id.count().desc());
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
                                            review.score.avg().coalesce(0.0),
                                            review.id.count(),
                                            shop.minOrderPrice,
                                            shop.defaultDeliveryPrice,
                                            distance.round().intValue()
                                                    ))
                .from(shop)
                .leftJoin(shop.reviews, review)
                .where(
                        cursorValue(request.getCursorValue(), shopSortType, distance, request.getCursorId()),
                        isDeletedFalse(),
                        eqCategoryId(request.getCategoryId()),
                        distance.round().intValue().lt(1000),
                        containsSearchKeyword(request.getSearchKeyword())
                )
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .groupBy(shop)
                .limit(request.getSize())
                .fetch();

        Long nextCursorId = calcNextCursorId(result);
        String nextCursorValue = calcNextCursorValue(result, shopSortType);

        return new ViewShopListResponse(result, nextCursorId, nextCursorValue);
    }

    @Override
    public Boolean existShopNameBySeller(String shopName, Long sellerId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(shop)
                .where(
                        shop.name.eq(shopName),
                        shop.seller.id.eq(sellerId)
                )
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public Boolean checkRegisterShopSellerByMenu(Long menuId, Long sellerId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(shop)
                .join(shop.menuGroups, menuGroup)
                .join(menuGroup.menus, menu)
                .where(
                        menu.id.eq(menuId),
                        shop.seller.id.eq(sellerId)
                )
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public Boolean checkRegisterShopSellerByOption(Long optionId, Long sellerId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(shop)
                .join(shop.menuGroups, menuGroup)
                .join(menuGroup.menus, menu)
                .join(menu.options, option)
                .where(
                        option.id.eq(optionId),
                        shop.seller.id.eq(sellerId)
                )
                .fetchFirst();
        return fetchOne != null;
    }

    @Override
    public Boolean checkRegisterShopSellerByOptionItem(Long optionId, Long sellerId) {
        Integer fetchOne = jpaQueryFactory
                .selectOne()
                .from(shop)
                .join(shop.menuGroups, menuGroup)
                .join(menuGroup.menus, menu)
                .join(menu.options, option)
                .join(option.optionItems, optionItem)
                .where(
                        optionItem.id.eq(optionId),
                        shop.seller.id.eq(sellerId)
                )
                .fetchFirst();
        return fetchOne != null;
    }

    private BooleanExpression ltShopId(Long shopId) {
        if (shopId != null && shopId > 0) {
            return shop.id.lt(shopId);
        }
        return null;
    }

    private BooleanExpression eqCategoryId(Long categoryId) {
        if (categoryId != null && categoryId > 0) {
            return shop.category.id.eq(categoryId);
        }
        return null;
    }

    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            return shop.name.contains(searchKeyword);
        }
        return null;
    }

    private BooleanExpression isDeletedFalse() {
        return shop.isDeleted.eq(false);
    }

    private NumberExpression<Double> calcDistance(Double latitude, Double longitude) {
        return numberTemplate(Double.class,
                "ST_Distance_Sphere(point({0}, {1}), point({2}, {3}))",
                longitude, latitude, shop.address.longitude, shop.address.latitude);
    }

    private BooleanExpression cursorValue(String cursorValue, ShopSortType shopSortType, NumberExpression<Double> distance, Long cursorId) {
        if (cursorValue == null || cursorId == null) return null;
        switch (shopSortType) {
            case DISTANCE:
                return distance.round().intValue().gt(Integer.valueOf(cursorValue)).or(
                        distance.round().intValue().eq(Integer.valueOf(cursorValue))
                                .and(shop.id.gt(cursorId))
                );
            case AVG_RATING:
                return review.score.avg().lt(Double.valueOf(cursorValue)).or(
                        review.score.avg().eq(Double.valueOf(cursorValue))
                                .and(shop.id.gt(cursorId))
                );
            case COUNT_REVIEW:
                return review.count().gt(Long.valueOf(cursorValue)).or(
                        review.count().eq(Long.valueOf(cursorValue))
                                .and(shop.id.gt(cursorId))
                );
            default:
                // 기본 정렬 설정: 거리, 평균 별점이 같은 경우 최신 순으로
                return ltShopId(cursorId);
        }
    }
    @TimeTrace
    private Long calcNextCursorId(List<ShopList> result) {
        return result.isEmpty() ? null : result.stream()
                .mapToLong(ShopList::getShopId)
                .min()
                .orElse(0L);
    }
    @TimeTrace
    private String calcNextCursorValue(List<ShopList> result, ShopSortType shopSortType) {
        switch (shopSortType) {
            case DISTANCE:
                return result.isEmpty() ? null : String.valueOf(result.get(result.size() - 1).getDistance());
            case AVG_RATING:
                return result.isEmpty() ? null : String.valueOf(result.get(result.size() - 1).getAvgRating());
            case COUNT_REVIEW:
                return result.isEmpty() ? null : String.valueOf(result.get(result.size() - 1).getReviewCount());
            default:
                // 기본 정렬 설정: 거리, 평균 별점이 같은 경우 최신 순으로
                return null;
        }
    }

}

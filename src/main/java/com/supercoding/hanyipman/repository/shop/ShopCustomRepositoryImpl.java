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
                .where(
                        isDeletedFalse(),
                        ltShopId(request.getCursor()),
                        eqCategoryId(request.getCategoryId()),
                        distance.round().intValue().lt(1000),
                        containsSearchKeyword(request.getSearchKeyword())
                )
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
                .limit(request.getSize())
                .fetch();

        Long nextCursor = calcNextCursor(result);

        return new ViewShopListResponse(result, nextCursor);
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

    private Long calcNextCursor(List<ShopList> result) {
        return result.isEmpty() ? null : result.get(result.size() - 1).getShopId();
    }
}

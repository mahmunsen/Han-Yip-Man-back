package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.Shop.seller.request.RegisterShopRequest;
import com.supercoding.hanyipman.dto.address.request.ShopAddressRequest;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.repository.CategoryRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.repository.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ShopTestDateService {

    @Autowired
    private SellerShopService sellerShopService;

    @Autowired
    private UserRepository userRepository;


    @Test
    public void testSaveRandomLoc() {
        double baseLatitude = 37.493871; // 기준 위도
        double baseLongitude = 127.014375; // 기준 경도
        int numberOfLocations = 10000; // 생성할 데이터 수

        List<ShopAddressRequest> addressRequestList =new ArrayList<>();
        List<RegisterShopRequest> registerShopRequests = new ArrayList<>();
        User user = userRepository.findById(9L).orElseThrow(()->new CustomException(UserErrorCode.NON_EXISTENT_MEMBER));
        for (int i = 0; i < numberOfLocations; i++) {
            Random random = new Random();
            ShopAddressRequest shopAddressRequest = new ShopAddressRequest();
            shopAddressRequest.setAddress("Address"+1422+i);
            shopAddressRequest.setAddressDetail("AddressDetail"+1422+i);
            shopAddressRequest.setLatitude(baseLatitude + Math.random() * 0.009);
            shopAddressRequest.setLongitude(baseLongitude + Math.random() * 0.009);
            addressRequestList.add(shopAddressRequest);

            RegisterShopRequest registerShopRequest = new RegisterShopRequest();
            registerShopRequest.setShopName("testShopName" + 1422+i);
            registerShopRequest.setShopPhone("010-0000-0000");
            registerShopRequest.setBusinessNumber("12-123-12345");
            registerShopRequest.setMinOrderPrice(19000);
            registerShopRequest.setCategoryId((long) (random.nextInt(10) + 1));
            registerShopRequest.setShowDescription("testDescription"+1422+i);

            registerShopRequests.add(registerShopRequest);

            sellerShopService.registerShop(registerShopRequest, shopAddressRequest, null, null, user);
        }
    }


}

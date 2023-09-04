package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class AuthServiceTest {
    @MockBean
    private SellerRepository sellerRepository;

    @Test
    void sellerSignup(SellerSignUpRequest request) {

        boolean existSellerEmail = sellerRepository.existSellerEmail(request.getEmail());

        System.out.println(existSellerEmail);
    }

}
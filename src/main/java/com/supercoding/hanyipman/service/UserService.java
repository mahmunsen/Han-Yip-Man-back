package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import com.supercoding.hanyipman.dto.user.request.LoginRequest;
import com.supercoding.hanyipman.dto.user.response.LoginResponse;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.error.domain.LoginErrorCode;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final AddressRepository addressRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public String sellerSignup(SellerSignUpRequest request) {
        Optional<User> userEntity = userRepository.findByEmail(request.getEmail());
        //TODO 유저 권한에 따른 예외처리 해야함
        if (!Objects.equals(request.getPassword(), request.getPasswordCheck()))
            throw new CustomException(UserErrorCode.INVALID_PASSWORD_CONFIRMATION);
        if (userEntity.isPresent()) throw new CustomException(UserErrorCode.DUPLICATE_MEMBER_ID);

        String encodingPassword = passwordEncoder.encode(request.getPassword());

        User user = User.toSellerSignup(request, encodingPassword);
        User savedUser = userRepository.save(user);
        Seller savedSeller = Seller.toSeller(request, savedUser);
        sellerRepository.save(savedSeller);

        return savedUser.getEmail();
//        switch (userEntity.getRole()) {
//            case "SELLER":
//                break;
//            case "BUYER":
//                break;
//            case "ALL":
//                break;

    }

    @Transactional
    public String buyersSignup(BuyerSignUpRequest request) {
        Optional<User> userEntity = userRepository.findByEmail(request.getEmail());
        // TODO 유저 권한에 따른 예외처리 해야함
        if (!Objects.equals(request.getPassword(), request.getPasswordCheck()))
            throw new CustomException(UserErrorCode.INVALID_PASSWORD_CONFIRMATION);
        if (userEntity.isPresent()) throw new CustomException(UserErrorCode.DUPLICATE_MEMBER_ID);

        String encodingPassword = passwordEncoder.encode(request.getPassword());

        User user = User.toBuyerSignup(request, encodingPassword);
        User savedUser = userRepository.save(user);

        Buyer buyer = Buyer.tobuyer(savedUser, request);
        Buyer savedBuyUser = buyerRepository.save(buyer);
//      주소 지움
//        Address address = Address.toBuyerAddress(request, savedBuyUser);
//        addressRepository.save(address);
        return savedUser.getEmail();
    }


    public User login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty())
            throw new CustomException(LoginErrorCode.INVALID_LOGIN);

        if (Boolean.TRUE.equals(optionalUser.get().getIsDeleted()))
            throw new CustomException(LoginErrorCode.INVALID_LOGIN);

        User loginUser = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), loginUser.getPassword())) {
            throw new CustomException(LoginErrorCode.INVALID_LOGIN);
        }
        return loginUser;
    }

    public LoginResponse tokenGenerator(User loginUser) {
        //TODO: 판매자 아니면 예외처리
        LoginResponse loginResponse;

        String jwtToken = JwtTokenProvider.createToken(loginUser);

        if (loginUser.getRole().equalsIgnoreCase("SELLER")) {
            Seller getLoginUser = sellerRepository.findByUser(loginUser);
            loginResponse = LoginResponse.toLoginSellerResponse(loginUser, jwtToken, getLoginUser);
        } else {
            Buyer getLoginUser = buyerRepository.findByUser(loginUser);
            loginResponse = LoginResponse.toLoginBuyerResponse(loginUser, jwtToken, getLoginUser);
        }
        return loginResponse;
    }


}

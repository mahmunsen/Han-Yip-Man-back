package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import com.supercoding.hanyipman.dto.user.request.LoginRequest;
import com.supercoding.hanyipman.dto.user.response.LoginResponse;
import com.supercoding.hanyipman.entity.*;
import com.supercoding.hanyipman.enums.FilePath;
import com.supercoding.hanyipman.error.domain.FileErrorCode;
import com.supercoding.hanyipman.error.domain.LoginErrorCode;
import com.supercoding.hanyipman.error.domain.SellerErrorCode;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.UserErrorCode;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final AddressRepository addressRepository;
    private final AwsS3Service awsS3Service;

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
    public String buyersSignup(BuyerSignUpRequest request, MultipartFile file) {
        Optional<User> userEntity = userRepository.findByEmail(request.getEmail());
        // TODO 유저 권한에 따른 예외처리 해야함
        if (request.getPassword() == request.getPasswordCheck())
            throw new CustomException(UserErrorCode.INVALID_PASSWORD_CONFIRMATION);
        if (userEntity.isPresent()) throw new CustomException(UserErrorCode.DUPLICATE_MEMBER_ID);

        String encodingPassword = passwordEncoder.encode(request.getPassword());

        User user = User.toBuyerSignup(request, encodingPassword);
        userRepository.save(user);
        Buyer buyer = Buyer.tobuyer(user, request, uploadImageFile(file, user));
        Buyer savedBuyUser = buyerRepository.save(buyer);
        Address address = Address.toBuyerAddress(request, savedBuyUser);
        addressRepository.save(address);
        return user.getEmail();
    }


    public User login(LoginRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) throw new CustomException(LoginErrorCode.INVALID_LOGIN);

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
            Seller getLoginUser = sellerRepository.findByUser(loginUser).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
            loginResponse = LoginResponse.toLoginSellerResponse(loginUser, jwtToken, getLoginUser);
        } else {
            Buyer getLoginUser = buyerRepository.findByUser(loginUser);
            loginResponse = LoginResponse.toLoginBuyerResponse(loginUser, jwtToken, getLoginUser);
        }
        return loginResponse;
    }

    private String uploadImageFile(MultipartFile multipartFile, User user) {
        String uniqueIdentifier = UUID.randomUUID().toString();
        try {
            if (multipartFile != null) {
                return awsS3Service.uploadImage(multipartFile, FilePath.TEST_DIR.getPath() + user.getId() + "/" + uniqueIdentifier);
            }
        } catch (IOException e) {
            throw new CustomException(FileErrorCode.FILE_UPLOAD_FAILED);
        }
        return null;
    }

    public void checkDuplicateEmail(String checkEmail) {
        Optional<User> user = userRepository.findByEmail(checkEmail);
        if (user.isPresent())throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);

    }
}

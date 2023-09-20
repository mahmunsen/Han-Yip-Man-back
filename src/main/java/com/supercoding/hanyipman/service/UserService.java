package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.user.request.BuyerSignUpRequest;
import com.supercoding.hanyipman.dto.user.request.LoginRequest;
import com.supercoding.hanyipman.dto.user.request.SellerSignUpRequest;
import com.supercoding.hanyipman.dto.user.request.SignUpRequest;
import com.supercoding.hanyipman.dto.user.response.LoginResponse;
import com.supercoding.hanyipman.entity.Address;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.Seller;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.enums.FilePath;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.*;
import com.supercoding.hanyipman.repository.AddressRepository;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.SellerRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    // Bean으로 등록된 BCryptPasswordEncoder 의존성 주입
    private final PasswordEncoder passwordEncoder;

    // 로그인 후 유저 반환
    public User login(LoginRequest loginInfo) {
        User user = findValidUserByEmail(loginInfo);
        if (isNotDeletedUser(user) || isMatchesPassword(loginInfo.getPassword(), user.getPassword()))
            throw new CustomException(LoginErrorCode.INVALID_LOGIN);
        return user;
    }

    // 토큰 발급 후 토큰과 유저 정보 반환
    public LoginResponse tokenGenerator(User loginUser) {
        String jwtToken = JwtTokenProvider.createToken(loginUser);
        return getRoleLoginResponse(loginUser, jwtToken);
    }

    // 판매자 회원가입
    @Transactional
    public String sellerSignup(SellerSignUpRequest request) {
        checkUserAndPassword(request);
        User user = User.toSellerSignup(request, getEncryptionPassword(request));
        userRepository.save(user);
        Seller savedSeller = Seller.toSeller(request, user);
        sellerRepository.save(savedSeller);

        return user.getEmail();
    }

    // 구매자 회원가입
    @Transactional
    public String buyersSignup(BuyerSignUpRequest request, MultipartFile file) {
        checkUserAndPassword(request);
        User user = User.toBuyerSignup(request, getEncryptionPassword(request));
        userRepository.save(user);
        Buyer buyer = Buyer.toBuyer(user, uploadImageFile(file, user));
        buyerRepository.save(buyer);
        Address address = Address.toBuyerAddress(request, buyer);
        addressRepository.save(address);
        return user.getEmail();
    }

    // 이메일 중복체크
    public void checkDuplicateEmail(String checkEmail) {
        Optional<User> user = userRepository.findByEmail(checkEmail);
        if (user.isPresent()) throw new CustomException(UserErrorCode.DUPLICATE_EMAIL);
    }

    // 토큰 암호화
    private String getEncryptionPassword(SignUpRequest request) {
        String encodingPassword = passwordEncoder.encode(request.getPassword());
        return encodingPassword;
    }

    // 유저 예외처리, 비밀번화와 비밀번호 확인 값 체크
    private void checkUserAndPassword(SignUpRequest request) {
        Optional<User> userEntity = userRepository.findByEmail(request.getEmail());
        if (userEntity.isPresent()) throw new CustomException(UserErrorCode.DUPLICATE_MEMBER_ID);
        if (!Objects.equals(request.getPassword(), request.getPasswordCheck()))
            throw new CustomException(UserErrorCode.INVALID_PASSWORD_CONFIRMATION);
    }

    // 프로필 사진 업로드
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

    // 이메일에 맞는 유저 반환
    private User findValidUserByEmail(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new CustomException(LoginErrorCode.INVALID_LOGIN));
    }

    // 유저의 탈퇴 여부 확인(true: 탈퇴, false: 유저)
    private static boolean isNotDeletedUser(User user) {
        return Boolean.TRUE.equals(user.getIsDeleted());
    }

    // 매개변수로 들어온 2개의 비밀번호 동일여부 확인
    private boolean isMatchesPassword(String loginPw, String userPw) {
        return !passwordEncoder.matches(loginPw, userPw);
    }

    // 유저 권한에 따른 분기 처리
    private LoginResponse getRoleLoginResponse(User loginUser, String jwtToken) {

        if (loginUser.getRole().equalsIgnoreCase("SELLER")) {
            sellerRepository.findByUser(loginUser).orElseThrow(() -> new CustomException(SellerErrorCode.NOT_SELLER));
            return LoginResponse.toLoginSellerResponse(loginUser, jwtToken);
        }
        if (loginUser.getRole().equalsIgnoreCase("BUYER")) {
            Buyer getLoginUser = buyerRepository.findByUser(loginUser).orElseThrow(() -> new CustomException(BuyerErrorCode.NOT_BUYER));
            return LoginResponse.toLoginBuyerResponse(loginUser, jwtToken, getLoginUser);
        }
        throw new CustomException(UserErrorCode.FORBIDDEN_ACCESS);
    }
}

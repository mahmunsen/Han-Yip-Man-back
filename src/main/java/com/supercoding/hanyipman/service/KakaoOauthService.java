package com.supercoding.hanyipman.service;

import com.supercoding.hanyipman.dto.user.response.KakaoTokenResponse;
import com.supercoding.hanyipman.dto.user.response.KakaoUserInfoResponse;
import com.supercoding.hanyipman.dto.user.response.LoginResponse;
import com.supercoding.hanyipman.entity.Buyer;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.KakaoLoginErrorCode;
import com.supercoding.hanyipman.repository.BuyerRepository;
import com.supercoding.hanyipman.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class KakaoOauthService {

    @Value("${kakao.kakaoClientId}")
    private String kakaoClientId;
    @Value("${kakao.kakaoSecretKey}")
    private String kakaoSecretKey;
    @Value("${kakao.kakaoRedirectUri}")
    private String kakaoRedirectUri;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepository userRepository;
    private final UserService userService;
    private final BuyerRepository buyerRepository;

    @Transactional
    public LoginResponse kakaoLoginOauth(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("Accept", "application/json");

            KakaoTokenResponse tokenResponse = restTemplate.postForObject("https://kauth.kakao.com/oauth/token?grant_type=authorization_code" + "&client_id=" + kakaoClientId + "&client_secret=" + kakaoSecretKey + "&redirect_uri=" + kakaoRedirectUri + "&code=" + code, null, KakaoTokenResponse.class);

            if (tokenResponse != null) {
                // 액세스 토큰으로 유저 정보 요청
                headers.setBearerAuth(tokenResponse.getAccess_token());

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, entity, KakaoUserInfoResponse.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    KakaoUserInfoResponse userInfo = response.getBody();

                    String userId = userInfo.getId();
                    String nickname = userInfo.getProperties().getNickname();
                    String email = userInfo.getKakao_account().getEmail();
                    String profileImage = userInfo.getProperties().getProfile_image();
                    /*
                     * 1. 이메일이 있는지 확인한다.
                     * 2. 이메일이 없다 -> 이메일로 카카오전용 회원가입 진행(이메일 프로필 사진)
                     * 3. 이메일이 있다 -> 해당 이메일로 로그인하며 토큰 발급하여 로그인과 동일하게 토큰 발급하여 프론트로 전송
                     * 예외처리 할 것 : 카카오로 회원가입 하고 일반 이메일로 로그인 한다면? 어떻게 막을 것인가?
                     * */

                    Optional<User> optionalUser = userRepository.findByEmailAndBuyer(email);

                    if (optionalUser.isEmpty()) {
//                        throw new CustomException(KakaoLoginErrorCode.NON_EXISTENT_MEMBER);
                        // 유저 아이디를 비밀번호로 임시지정
                        String encodingPassword = passwordEncoder.encode(userId);
                        User user = User.kakaoBuyerSignup(userInfo, encodingPassword);
                        userRepository.save(user);
                        Buyer buyer = Buyer.toBuyer(user, profileImage);
                        buyerRepository.save(buyer);
                        System.out.println(nickname + "카카오 회원가입 성공");

                        // 회원가입 후 로그인
                        LoginResponse loginResponse = userService.tokenGenerator(user);
                        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.getAccessToken());
                        return loginResponse;
                    }


                    // 해당 이메일로 가입된 기존 회원일 경우
                    LoginResponse loginResponse = userService.tokenGenerator(optionalUser.get());
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.getAccessToken());
                    return loginResponse;

                }
            } else {
                System.out.println("tokenResponse요청 실패");
            }
            return null;
        } catch (HttpClientErrorException.BadRequest badRequest) {
            throw new CustomException(KakaoLoginErrorCode.INVALID_KAKAO_LOGIN_CODE);
        }
    }
}

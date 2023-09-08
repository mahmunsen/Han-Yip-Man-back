package com.supercoding.hanyipman.security;

import com.supercoding.hanyipman.dto.user.CustomUserDetail;
import com.supercoding.hanyipman.entity.User;
import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.domain.LoginErrorCode;
import com.supercoding.hanyipman.error.domain.TokenErrorCode;
import com.supercoding.hanyipman.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Getter
@NoArgsConstructor
@Configuration
public class JwtToken {
    private static UserRepository userRepository;

    @Autowired
    public JwtToken(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static synchronized User user() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            // 게스트 권한이면 예외처리
            if (principal == "anonymousUser") throw new CustomException(TokenErrorCode.ACCESS_DENIED);

            // 토큰데이터 확인 유저객체 return
            if (principal instanceof UserDetails) {
                CustomUserDetail userDetails = (CustomUserDetail) principal;
                return userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(TokenErrorCode.ACCESS_DENIED));
            }
        }
        return null;
    }
}
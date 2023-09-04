package com.supercoding.hanyipman.security.filters;

import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.ErrorCode;
import com.supercoding.hanyipman.error.domain.TokenErrorCode;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwtToken = resolveToken(request);
        String requestURI = request.getRequestURI();
        try {
            // 유효성 검증
            if (jwtToken != null && jwtTokenProvider.validateToken(jwtToken)) {
                // 토큰 값 뽑아 스프링 시큐리티 유저를 만들어서 Authentication 반환
                Authentication auth = jwtTokenProvider.getAuthentication(jwtToken);
                // 해당 스프링 시큐리티 유저를 시큐리티 컨텍스트에 저장, 즉 디비를 거치지 않음
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // 현재 사용자의 보안 컨텍스트를 초기화하고 사용자의 인증을 해제
            SecurityContextHolder.clearContext();
            throw new CustomException(TokenErrorCode.INVALID_TOKEN);
        }
        // 다음 필터 체인 실행
        filterChain.doFilter(request, response);
    }

    // 헤더에서 토큰 정보를 꺼내온다.
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}

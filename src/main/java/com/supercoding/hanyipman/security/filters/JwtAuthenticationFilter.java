package com.supercoding.hanyipman.security.filters;

import com.supercoding.hanyipman.error.CustomException;
import com.supercoding.hanyipman.error.ErrorCode;
import com.supercoding.hanyipman.error.domain.TokenErrorCode;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
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
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String jwtToken = resolveToken(request);
        String requestURI = request.getRequestURI();

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authorizationHeader.substring(7);

        jwtTokenProvider.validateToken(jwtToken);
        Authentication auth = jwtTokenProvider.getAuthentication(jwtToken);
                    // 해당 스프링 시큐리티 유저를 시큐리티 컨텍스트에 저장, 즉 디비를 거치지 않음
        SecurityContextHolder.getContext().setAuthentication(auth);

//        SecurityContextHolder.clearContext();
        // 다음 필터 체인 실행
        filterChain.doFilter(request, response);
    }
}

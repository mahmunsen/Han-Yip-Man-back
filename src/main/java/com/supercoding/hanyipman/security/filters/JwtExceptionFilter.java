package com.supercoding.hanyipman.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.error.ErrorCode;
import com.supercoding.hanyipman.error.domain.TokenErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);//JwtAuthenticationFilter로 이동
        } catch (ExpiredJwtException e) {
            setErrorResponse(request, response, TokenErrorCode.EXPIRED_JWT_TOKEN);
        } catch (AccessDeniedException e) {
            setErrorResponse(request, response, TokenErrorCode.INVALID_JWT_SIGNATURE);
        } catch (JwtException ex) {
            // JwtAuthenticationFilter에서 예외 발생하면 호출
            setErrorResponse(request, response, TokenErrorCode.INVALID_JWT_TOKEN);
        }
    }

    public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, ErrorCode errorCode) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        res.setStatus(errorCode.getCode());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");

        Response errorResponse = new Response(false, errorCode.getCode(), errorCode.getMessage(), null);

        // 에러 응답 생성
         res.getWriter().write(objectMapper.writeValueAsString(errorResponse));

    }
}

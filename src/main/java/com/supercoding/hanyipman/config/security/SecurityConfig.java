package com.supercoding.hanyipman.config.security;

import com.supercoding.hanyipman.security.JwtAccessDeniedHandler;
import com.supercoding.hanyipman.security.JwtAuthenticationEntryPoing;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import com.supercoding.hanyipman.security.UserRole;
import com.supercoding.hanyipman.security.filters.JwtAuthenticationFilter;
import com.supercoding.hanyipman.security.filters.JwtExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@RequiredArgsConstructor
@EnableWebSecurity // 스프링 시큐리티 등등 기본적인 웹보안 활성화
public class SecurityConfig {

    private final JwtAuthenticationEntryPoing jwtAuthenticationEntryPoing;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and() // CORS설정 활성화 다른 도메인에서 요청 허용
                .csrf().disable() // CSRF 기능 비활성화(토큰 기반 인증 방식으로 필요하지 않음)
                .addFilterBefore(new JwtExceptionFilter(), UsernamePasswordAuthenticationFilter.class) // JWT 예외처리
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)// JWT 반환
                .sessionManagement()// 세션 설정을 명시
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 세션 방식 사용하지 않음
                .and()
                .authorizeHttpRequests()// HTTP 요청에 대한 설정을 명시
                .antMatchers("/**").permitAll()// 앞 패턴에 대해 모든 사용자 접근 가능
                .antMatchers("/api/**").hasAuthority(UserRole.BUYER.name())// 앞 패턴에 대해 BUYER 권한 사용자만 접근 가능
                .antMatchers("/api/**").hasAuthority(UserRole.SELLER.name())// 앞 패턴에 대해 SELLER 권한 사용자만 접근 가능
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoing)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        ;

        return httpSecurity.build();
    }
}
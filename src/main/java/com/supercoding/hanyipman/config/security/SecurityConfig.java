package com.supercoding.hanyipman.config.security;

import com.supercoding.hanyipman.security.JwtAccessDeniedHandler;
import com.supercoding.hanyipman.security.JwtAuthenticationEntryPoing;
import com.supercoding.hanyipman.security.JwtTokenProvider;
import com.supercoding.hanyipman.security.filters.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@AllArgsConstructor
@EnableMethodSecurity // @PreAuthorize 어노테이션 사용 선언
@EnableWebSecurity // 스프링 시큐리티 등등 기본적인 웹보안 활성화
public class SecurityConfig {
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoing jwtAuthenticationEntryPoing;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;


    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter authenticationFilter) throws Exception {
        httpSecurity
                .cors().and() // 찾아보기
                .csrf().disable() // token을 사용하는 방식이므로 csrf 해제
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoing)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
//                        .authorizeRequests()
                .authorizeHttpRequests()// 요청에 대한 권한 설정
                .antMatchers("/**").permitAll()
//                .antMatchers("/swagger-ui").permitAll()
//                .antMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
//                .antMatchers("/**").permitAll()
//                .anyRequest().permitAll()

                .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;

        return httpSecurity.build();
    }
}

/*
.antMatchers()
: 페이지에 접근할 수 있는 권한을 설정한다.
.loginPage
: 로그인 페이지
.loginProcessingUrl
: 구현한 로그인 페이지
defaultSuccessUrl
: 로그인 성공 시 제공할 페이지
failureUrl
: 로그인 실패 시 제공할 페이지
csrf().disable()
: 사이트 간 요청 위조(Cross-Site Request Forgery) 공격 방지 기능 키기
*/

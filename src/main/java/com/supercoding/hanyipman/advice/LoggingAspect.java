package com.supercoding.hanyipman.advice;

import com.supercoding.hanyipman.dto.vo.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LoggingAspect {

    private final HttpServletRequest request;


    @Before("execution(* com.supercoding.hanyipman..*Controller.*(..))")
    public void beforeAdvice() {
        String endpoint = request.getRequestURI(); // 현재 요청의 엔드포인트 경로를 얻어옴

        log.info("\u001B[34mAPI 호출! - "+endpoint + "\u001B[0m");
    }

    @AfterReturning(pointcut  = "execution(* com.supercoding.hanyipman..*Controller.*(..))",  returning = "returnValue")
    public void afterAdvice(Object returnValue) {
        String endpoint = request.getRequestURI(); // 현재 요청의 엔드포인트 경로를 얻어옴

        log.info("\u001B[32mAPI 호출 완료! - "+endpoint + " : "+ ((Response<?>) returnValue).getMessage() +"\u001B[0m");
    }

}
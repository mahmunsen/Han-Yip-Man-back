package com.supercoding.hanyipman.advice;

import com.supercoding.hanyipman.dto.vo.Response;
import com.supercoding.hanyipman.error.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {

        // 에러 코드 설정
        Response errorResponse = new Response(false, HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);

        // 에러 응답 생성
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(errorResponse);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response> handleCustomException(CustomException e) {
        // 에러 정보를 담은 ErrorResponse 객체 생성
        Response errorResponse = new Response(false, e.getErrorCode().getCode(), e.getErrorMessage(), null);

        // 에러 응답 생성
        return ResponseEntity.status(e.getErrorCode().getCode()).body(errorResponse);
    }
}

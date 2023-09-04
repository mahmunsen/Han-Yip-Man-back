package com.supercoding.hanyipman.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    int getCode();

    String getMessage();
}
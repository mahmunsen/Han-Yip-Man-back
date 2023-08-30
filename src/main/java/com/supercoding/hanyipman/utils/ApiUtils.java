package com.supercoding.hanyipman.utils;

import com.supercoding.hanyipman.dto.vo.Response;

public class ApiUtils {

    public static <T> Response<T> success(Integer status, String message, T data) {

        return new Response<>(true, status, message, data);

    }

}

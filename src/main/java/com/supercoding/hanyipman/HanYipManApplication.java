package com.supercoding.hanyipman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

//@EnableCaching
@SpringBootApplication
public class HanYipManApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanYipManApplication.class, args);
    }

}

package com.supercoding.hanyipman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

//@EnableCaching
@SpringBootApplication
@EnableAsync
public class HanYipManApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanYipManApplication.class, args);
    }

}

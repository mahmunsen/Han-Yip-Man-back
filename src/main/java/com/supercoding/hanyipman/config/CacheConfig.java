package com.supercoding.hanyipman.config;

import com.supercoding.hanyipman.cache.CacheKeyGenerator;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    // "viewOrderDetail"이라는 이름의 캐시를 사용하여 메소드 호출 결과를 캐싱하고 관리하기 위한 CacheManager를 설정
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        Cache viewOrderDetailCache = new ConcurrentMapCache("viewOrderDetail");
        cacheManager.setCaches(Arrays.asList(viewOrderDetailCache));
        return cacheManager;
    }
    @Bean("cacheKeyGenerator")
    public KeyGenerator keyGenerator(){
        return new CacheKeyGenerator();
    }
}

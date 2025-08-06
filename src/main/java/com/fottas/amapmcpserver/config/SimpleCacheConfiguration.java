package com.fottas.amapmcpserver.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 *
 * @author yinh
 */
@Configuration
@EnableCaching
public class SimpleCacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("amapCache");
        // 关键配置：启用异步缓存模式支持响应式返回值
        cacheManager.setAsyncCacheMode(true);
        return cacheManager;
    }
}
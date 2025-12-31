package com.mypropertyfact.estate.configs;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the application.
 * Uses ConcurrentMapCacheManager for in-memory caching.
 * For production, consider using Redis or Caffeine for better performance.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache manager bean configuration.
     * Defines cache names used throughout the application.
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        // Define cache names
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "projects",           // Cache for getAllProjects
            "projectInfo"         // Cache for project info DTOs
        ));
        return cacheManager;
    }
}


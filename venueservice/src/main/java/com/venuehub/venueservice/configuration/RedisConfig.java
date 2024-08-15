package com.venuehub.venueservice.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RedisConfig {

    private RedissonClient redissonClient;

//
//    @Value("${redis.url}")
//    private String RENDER_REDIS_URL;

    @Bean
    public RedissonClient getClient(@Value("${render.redis.url}") String RENDER_REDIS_URL) {

        if (RENDER_REDIS_URL == null || RENDER_REDIS_URL.isEmpty()) {
            throw new IllegalStateException("Environment variable RENDER_REDIS_URL is not set.");
        }
        if (Objects.isNull(redissonClient)) {
            org.redisson.config.Config config = new org.redisson.config.Config();
            config.useSingleServer()
                    .setConnectionPoolSize(4)
                    .setConnectionMinimumIdleSize(1)
                    .setAddress(RENDER_REDIS_URL);

            redissonClient = Redisson.create(config);
        }
        return redissonClient;

    }
}
package com.venuehub.bookingservice.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RedisConfig {

    private RedissonClient redissonClient;

    @Bean
    public RedissonClient getClient() {
        if (Objects.isNull(redissonClient)) {
            org.redisson.config.Config config = new org.redisson.config.Config();
            config.useSingleServer()
                    .setAddress("redis://red-cqv0khaj1k6c73dnu67g:6379");
            redissonClient = Redisson.create(config);
        }
        return redissonClient;

    }
}
package com.venuehub.imageservice.configuration;

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
                    .setAddress("redis://127.0.0.1:6379");
            redissonClient = Redisson.create(config);
        }
        return redissonClient;

    }
}
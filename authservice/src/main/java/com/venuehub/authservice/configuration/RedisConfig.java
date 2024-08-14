package com.venuehub.authservice.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class RedisConfig {

    private RedissonClient redissonClient;

//    @Value("${redis.password}")
//    private String redisPassword;

    @Bean
    public RedissonClient getClient() {

//        if (redisPassword == null || redisPassword.isEmpty()) {
//            throw new IllegalStateException("Environment variable REDIS_PASSWORD is not set.");
//        }
//        String redisAddress = String.format("redis://default:%s@redis-13981.c275.us-east-1-4.ec2.redns.redis-cloud.com:13981", redisPassword);
//
        if (Objects.isNull(redissonClient)) {
            org.redisson.config.Config config = new org.redisson.config.Config();
            config.useSingleServer()
                    .setAddress("redis://127.0.0.1:6379");
//                    .setAddress(redisAddress);

            redissonClient = Redisson.create(config);
        }
        return redissonClient;

    }
}
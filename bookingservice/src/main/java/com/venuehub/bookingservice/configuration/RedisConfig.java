package com.venuehub.bookingservice.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Objects;

@Configuration
@Profile({"prod","dev"})
public class RedisConfig {

    private RedissonClient redissonClient;

    @Bean
    public RedissonClient getClient() {
        if (Objects.isNull(redissonClient)) {
            org.redisson.config.Config config = new org.redisson.config.Config();
            config.useSingleServer()
                    .setAddress("redis://127.0.0.1:6379")
                    //Increasing the connection timeout to 60 seconds - default is 3
                    .setTimeout(60000);
            redissonClient = Redisson.create(config);
        }
        return redissonClient;

    }
}
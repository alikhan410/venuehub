package com.venuehub.imageservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ResourceLoader;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.venuehub.imageservice", "com.venuehub.broker"})
//@EnableCaching
public class ImageserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageserviceApplication.class, args);
    }

    @Bean
    CommandLineRunner run(ResourceLoader resourceLoader) {
        return args -> {

        };
    }
}

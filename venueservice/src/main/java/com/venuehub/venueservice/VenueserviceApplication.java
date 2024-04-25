package com.venuehub.venueservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.venuehub.venueservice","com.venuehub.broker"})
public class VenueserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VenueserviceApplication.class, args);
    }

}

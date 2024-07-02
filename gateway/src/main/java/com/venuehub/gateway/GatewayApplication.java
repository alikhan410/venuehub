package com.venuehub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient

public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
//	@Bean
//	RouteLocator gateway(RouteLocatorBuilder rlb) {
//		return rlb
//				.routes()
//				.route(rs -> rs
//						.path("/")
//						.filters(GatewayFilterSpec::tokenRelay)
////                        .uri("http://google.com"))
//						.uri("http://localhost:8081/booking"))
//				.build();
//	}
}

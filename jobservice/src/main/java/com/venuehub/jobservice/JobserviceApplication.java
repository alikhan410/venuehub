package com.venuehub.jobservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.venuehub.jobservice","com.venuehub.broker"})
public class JobserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(JobserviceApplication.class, args);
	}

}

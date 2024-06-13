package com.venuehub.bookingservice;

import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.venuehub.bookingservice", "com.venuehub.broker"})
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }

    @Transactional
    @Bean
    CommandLineRunner run(VenueService venueService) {
        return args -> {
            if (!venueService.findAll().isEmpty()) {
                return;
            }
            List<Venue> venues = new ArrayList<>();
            Venue venue1 = new Venue(1L, "Emerald Banquet", 75000, "vendor");
            Venue venue2 = new Venue(2L, "Royal Hall", 150000, "vendor");
            Venue venue3 = new Venue(3L, "Blue Sky Banquet", 50000, "vendor");
            Venue venue4 = new Venue(4L, "Greenfield Hall", 100000, "vendor");
            Venue venue5 = new Venue(5L, "Grand Banquet", 200000, "vendor");
            venues.add(venue1);
            venues.add(venue2);
            venues.add(venue3);
            venues.add(venue4);
            venues.add(venue5);
            venueService.saveAll(venues);
        };
    }

}

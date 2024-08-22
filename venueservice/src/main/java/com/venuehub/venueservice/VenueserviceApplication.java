package com.venuehub.venueservice;

import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.venueservice.constant.VenueStatus;
import com.venuehub.venueservice.model.Image;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.service.ImageService;
import com.venuehub.venueservice.service.VenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.venuehub.venueservice", "com.venuehub.broker"})
//@EnableAsync
//@EnableCaching
public class VenueserviceApplication {
    public static final Logger logger = LoggerFactory.getLogger(VenueserviceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(VenueserviceApplication.class, args);
    }

    @Transactional
    @Bean
    CommandLineRunner run(VenueService venueService, ImageService ImageService) {

        return args -> {
            if (!venueService.findAll().isEmpty()) {
                return;
            }
            addTestVenues(venueService, ImageService);
        };

    }

    private void addTestVenues(VenueService venueService, ImageService ImageService) {

        createVenue("Banquet", "03001234567", 300, "A large banquet suitable for corporate events.", 75000, "Emerald Banquet", "Clifton, Karachi", 1L, "emerald-banquet", "eb", ImageService, venueService);
        createVenue("Hall", "03119876543", 500, "A grand hall with elegant decor.", 150000, "Royal Hall", "DHA Phase 5, Karachi", 2L, "royal-hall", "rh", ImageService, venueService);
        createVenue("Banquet", "03331234567", 200, "A compact banquet ideal for small weddings.", 50000, "Blue Sky Banquet", "PECHS, Karachi", 3L, "blue-sky-banquet", "bsb", ImageService, venueService);
        createVenue("Hall", "03455678901", 400, "A beautiful hall for various events.", 100000, "Greenfield Hall", "Gulshan-e-Iqbal, Karachi", 4L, "greenfield-hall", "gh", ImageService, venueService);
        createVenue("Banquet", "03551239876", 800, "A state-of-the-art banquet for large gatherings.", 200000, "Grand Banquet", "Nazimabad, Karachi", 5L, "grand-banquet", "gb", ImageService, venueService);

//        venueService.saveAll(venues);
    }

    private void createVenue(String venueType, String phone, int capacity, String description, int estimate, String name, String location, Long id, String uriPrefix, String initials, ImageService ImageService, VenueService venueService) {
        int rand = Math.abs(UUID.randomUUID().toString().hashCode());
//
        Image image1 = new Image(rand +10L,"0-"+initials,name, "https://venuehub-imagebucket.s3.eu-north-1.amazonaws.com/"+"0-"+initials+".jpg","vendor");
        Image image2 = new Image(rand +10L,"1-"+initials,name, "https://venuehub-imagebucket.s3.eu-north-1.amazonaws.com/"+"1-"+initials+".jpg","vendor");
        Image image3 = new Image(rand +10L,"2-"+initials,name, "https://venuehub-imagebucket.s3.eu-north-1.amazonaws.com/"+"2-"+initials+".jpg","vendor");

        Venue venue = Venue.builder()
                .venueType(venueType)
                .phone(phone)
                .capacity(capacity)
                .description(description)
                .estimate(estimate)
                .username("vendor")
                .status(VenueStatus.ACTIVE)
                .images(new ArrayList<>())
                .name(name)
                .location(location)
                .build();

        venueService.save(venue);
        Venue savedVenue = venueService.findById(venue.getId()).orElseThrow(NoSuchVenueException::new);
        savedVenue.getImages().add(image1);
        savedVenue.getImages().add(image2);
        savedVenue.getImages().add(image3);
        venueService.save(savedVenue);

    }
}


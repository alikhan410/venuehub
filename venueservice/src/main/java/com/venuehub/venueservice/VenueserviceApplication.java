package com.venuehub.venueservice;

import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.service.ImageDataService;
import com.venuehub.venueservice.service.VenueService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.venuehub.venueservice", "com.venuehub.broker"})
@EnableAsync
@EnableCaching
public class VenueserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VenueserviceApplication.class, args);
    }

    @Transactional
    @Bean
    CommandLineRunner run(VenueService venueService, ImageDataService imageDataService) {

        return args -> {
            if (!venueService.findAll().isEmpty()){
                return;
            }
            addTestVenues(venueService, imageDataService);
        };

    }

    private void addTestVenues(VenueService venueService, ImageDataService imageDataService) {
        List<Venue> venues = new ArrayList<>();

        Venue venue1 = Venue.builder()
                .venueType("Banquet")
                .phone("03001234567")
                .capacity(300)
                .description("A large banquet suitable for corporate events.")
                .estimate(75000)
                .username("vendor")
                .name("Emerald Banquet")
                .location("Clifton, Karachi")
                .build();

        Venue venue2 = Venue.builder()
                .venueType("Hall")
                .phone("03119876543")
                .capacity(500)
                .description("A grand hall with elegant decor.")
                .estimate(150000)
                .username("vendor")
                .name("Royal Hall")
                .location("DHA Phase 5, Karachi")
                .build();

        Venue venue3 = Venue.builder()
                .venueType("Banquet")
                .phone("03331234567")
                .capacity(200)
                .description("A compact banquet ideal for small weddings.")
                .estimate(50000)
                .username("vendor")
                .name("Blue Sky Banquet")
                .location("PECHS, Karachi")
                .build();

        Venue venue4 = Venue.builder()
                .venueType("Hall")
                .phone("03455678901")
                .capacity(400)
                .description("A beautiful hall for various events.")
                .estimate(100000)
                .username("vendor")
                .name("Greenfield Hall")
                .location("Gulshan-e-Iqbal, Karachi")
                .build();

        Venue venue5 = Venue.builder()
                .venueType("Banquet")
                .phone("03551239876")
                .capacity(800)
                .description("A state-of-the-art banquet for large gatherings.")
                .estimate(200000)
                .username("vendor")
                .name("Grand Banquet")
                .location("Nazimabad, Karachi")
                .build();

        venues.add(venue1);
        venues.add(venue2);
        venues.add(venue3);
        venues.add(venue4);
        venues.add(venue5);

        venueService.saveAll(venues);

        for (Venue venue : venues) {

            String venueName = venue.getName().toLowerCase().replace(" ","");

            byte[] b1 = loadImageAsBytes(String.format("static/%s-1.jpg", venueName));
            byte[] b2 = loadImageAsBytes(String.format("static/%s-2.jpg", venueName));
            byte[] b3 = loadImageAsBytes(String.format("static/%s-3.jpg", venueName));

            List<ImageData> images = new ArrayList<>();
            images.add(new ImageData(b1, venue));
            images.add(new ImageData(b2, venue));
            images.add(new ImageData(b3, venue));
            imageDataService.saveAll(images);
        }
    }

    private byte[] loadImageAsBytes(String resourcePath) {
        try {
            ClassPathResource imgFile = new ClassPathResource(resourcePath);
            InputStream inputStream = imgFile.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", byteArr);
            return byteArr.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}

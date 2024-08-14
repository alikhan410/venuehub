package com.venuehub.venueservice;

import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.venueservice.model.ImageUri;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.service.ImageUriService;
import com.venuehub.venueservice.service.VenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.venuehub.venueservice", "com.venuehub.broker"})
@EnableAsync
@EnableCaching
public class VenueserviceApplication {
    public static final Logger logger = LoggerFactory.getLogger(VenueserviceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(VenueserviceApplication.class, args);
    }

    @Transactional
    @Bean
    CommandLineRunner run(VenueService venueService, ImageUriService imageUriService) {

        return args -> {
            if (!venueService.findAll().isEmpty()) {
                return;
            }
            addTestVenues(venueService, imageUriService);
        };

    }

    private void addTestVenues(VenueService venueService, ImageUriService imageUriService) {
        List<Venue> venues = new ArrayList<>();

        createVenue("Banquet", "03001234567", 300, "A large banquet suitable for corporate events.", 75000, "Emerald Banquet", "Clifton, Karachi", 1L, "emerald-banquet", "eb", imageUriService, venueService);
        createVenue("Hall", "03119876543", 500, "A grand hall with elegant decor.", 150000, "Royal Hall", "DHA Phase 5, Karachi", 2L, "royal-hall", "rh", imageUriService, venueService);
        createVenue("Banquet", "03331234567", 200, "A compact banquet ideal for small weddings.", 50000, "Blue Sky Banquet", "PECHS, Karachi", 3L, "blue-sky-banquet", "bsb", imageUriService, venueService);
        createVenue("Hall", "03455678901", 400, "A beautiful hall for various events.", 100000, "Greenfield Hall", "Gulshan-e-Iqbal, Karachi", 4L, "greenfield-hall", "gh", imageUriService, venueService);
        createVenue("Banquet", "03551239876", 800, "A state-of-the-art banquet for large gatherings.", 200000, "Grand Banquet", "Nazimabad, Karachi", 5L, "grand-banquet", "gb", imageUriService, venueService);

//        venueService.saveAll(venues);
    }

    private void createVenue(String venueType, String phone, int capacity, String description, int estimate, String name, String location, Long id, String uriPrefix, String initials, ImageUriService imageUriService, VenueService venueService) {
        int rand = Math.abs(UUID.randomUUID().toString().hashCode());

        ImageUri imageUri1 = new ImageUri(rand + 10L, "/images/vendor/" + uriPrefix + "/0-" + initials);
        ImageUri imageUri2 = new ImageUri(rand + 20L, "/images/vendor/" + uriPrefix + "/1-" + initials);
        ImageUri imageUri3 = new ImageUri(rand + 30L, "/images/vendor/" + uriPrefix + "/2-" + initials);
        List<ImageUri> imageUris = new ArrayList<>(List.of(imageUri1, imageUri2, imageUri3));
        imageUriService.saveAll(imageUris);

        Venue venue = Venue.builder()
                .venueType(venueType)
                .phone(phone)
                .capacity(capacity)
                .description(description)
                .estimate(estimate)
                .username("vendor")
                .imageUris(new ArrayList<>())
                .imageUris(new ArrayList<>())
                .name(name)
                .location(location)
                .build();

        venueService.save(venue);
        Venue savedVenue = venueService.findById(venue.getId()).orElseThrow(NoSuchVenueException::new);
        savedVenue.getImageUris().add(imageUri1);
        savedVenue.getImageUris().add(imageUri2);
        savedVenue.getImageUris().add(imageUri3);
        venueService.save(savedVenue);
    }
}
//    private void addTestVenues(VenueService venueService) {
//        List<Venue> venues = new ArrayList<>();
//// Emerald Banquet Image URIs
//        ImageUri emeraldBanquetImageUri1 = new ImageUri(1L,"/images/vendor/emerald-banquet/0-eb");
//        ImageUri emeraldBanquetImageUri2 = new ImageUri(1L,"/images/vendor/emerald-banquet/1-eb");
//        ImageUri emeraldBanquetImageUri3 = new ImageUri(1L,"/images/vendor/emerald-banquet/2-eb");
//        List<ImageUri> uriList1 = new ArrayList<>(List.of(emeraldBanquetImageUri1, emeraldBanquetImageUri2, emeraldBanquetImageUri3));
//
//// Royal Hall Image URIs
//        ImageUri royalHallImageUri1 = new ImageUri(2L,"/images/vendor/royal-hall/0-rh");
//        ImageUri royalHallImageUri2 = new ImageUri(2L,"/images/vendor/royal-hall/1-rh");
//        ImageUri royalHallImageUri3 = new ImageUri(2L,"/images/vendor/royal-hall/2-rh");
//        List<ImageUri> uriList2 = new ArrayList<>(List.of(royalHallImageUri1, royalHallImageUri2, royalHallImageUri3));
//
//// Blue Sky Banquet Image URIs
//        ImageUri blueSkyBanquetImageUri1 = new ImageUri(3L,"/images/vendor/blue-sky-banquet/0-bs");
//        ImageUri blueSkyBanquetImageUri2 = new ImageUri(3L,"/images/vendor/blue-sky-banquet/1-bs");
//        ImageUri blueSkyBanquetImageUri3 = new ImageUri(3L,"/images/vendor/blue-sky-banquet/2-bs");
//        List<ImageUri> uriList3 = new ArrayList<>(List.of(blueSkyBanquetImageUri1, blueSkyBanquetImageUri2, blueSkyBanquetImageUri3));
//
//// Greenfield Hall Image URIs
//        ImageUri greenfieldHallImageUri1 = new ImageUri(4L,"/images/vendor/greenfield-hall/0-gh");
//        ImageUri greenfieldHallImageUri2 = new ImageUri(4L,"/images/vendor/greenfield-hall/1-gh");
//        ImageUri greenfieldHallImageUri3 = new ImageUri(4L,"/images/vendor/greenfield-hall/2-gh");
//        List<ImageUri> uriList4 = new ArrayList<>(List.of(greenfieldHallImageUri1, greenfieldHallImageUri2, greenfieldHallImageUri3));
//
//// Grand Banquet Image URIs
//        ImageUri grandBanquetImageUri1 = new ImageUri(5L,"/images/vendor/grand-banquet/0-gb");
//        ImageUri grandBanquetImageUri2 = new ImageUri(5L,"/images/vendor/grand-banquet/1-gb");
//        ImageUri grandBanquetImageUri3 = new ImageUri(5L,"/images/vendor/grand-banquet/2-gb");
//        List<ImageUri> uriList5 = new ArrayList<>(List.of(grandBanquetImageUri1, grandBanquetImageUri2, grandBanquetImageUri3));
//
//        Venue venue1 = Venue.builder()
//                .venueType("Banquet")
//                .phone("03001234567")
//                .capacity(300)
//                .description("A large banquet suitable for corporate events.")
//                .estimate(75000)
//                .username("vendor")
//                .imageUris(uriList1)
//                .name("Emerald Banquet")
//                .location("Clifton, Karachi")
//                .build();
//
//        Venue venue2 = Venue.builder()
//                .venueType("Hall")
//                .phone("03119876543")
//                .capacity(500)
//                .description("A grand hall with elegant decor.")
//                .estimate(150000)
//                .username("vendor")
//                .imageUris(uriList2)
//                .name("Royal Hall")
//                .location("DHA Phase 5, Karachi")
//                .build();
//
//        Venue venue3 = Venue.builder()
//                .venueType("Banquet")
//                .phone("03331234567")
//                .capacity(200)
//                .description("A compact banquet ideal for small weddings.")
//                .estimate(50000)
//                .username("vendor")
//                .imageUris(uriList3)
//                .name("Blue Sky Banquet")
//                .location("PECHS, Karachi")
//                .build();
//
//        Venue venue4 = Venue.builder()
//                .venueType("Hall")
//                .phone("03455678901")
//                .capacity(400)
//                .description("A beautiful hall for various events.")
//                .estimate(100000)
//                .username("vendor")
//                .imageUris(uriList4)
//                .name("Greenfield Hall")
//                .location("Gulshan-e-Iqbal, Karachi")
//                .build();
//
//        Venue venue5 = Venue.builder()
//                .venueType("Banquet")
//                .phone("03551239876")
//                .capacity(800)
//                .description("A state-of-the-art banquet for large gatherings.")
//                .estimate(200000)
//                .username("vendor")
//                .imageUris(uriList5)
//                .name("Grand Banquet")
//                .location("Nazimabad, Karachi")
//                .build();
//
//        venues.add(venue1);
//        venues.add(venue2);
//        venues.add(venue3);
//        venues.add(venue4);
//        venues.add(venue5);
//
//        venueService.saveAll(venues);
//
//    }
//}
//        for (Venue venue : venues) {
//
//            String venueName = venue.getName().toLowerCase().replace(" ","");
//
//            byte[] b1 = loadImageAsBytes(String.format("static/%s-1.jpg", venueName));
//            byte[] b2 = loadImageAsBytes(String.format("static/%s-2.jpg", venueName));
//            byte[] b3 = loadImageAsBytes(String.format("static/%s-3.jpg", venueName));
//
//            List<ImageData> images = new ArrayList<>();
//            images.add(new ImageData(b1, venue));
//            images.add(new ImageData(b2, venue));
//            images.add(new ImageData(b3, venue));
//            imageDataService.saveAll(images);
//        }
//    private byte[] loadImageAsBytes(String resourcePath) {
//        try {
//            ClassPathResource imgFile = new ClassPathResource(resourcePath);
//
//            InputStream inputStream = imgFile.getInputStream();
//            BufferedImage bufferedImage = ImageIO.read(inputStream);
//            ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage, "jpg", byteArr);
//            return byteArr.toByteArray();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new byte[0];
//        }
//    }

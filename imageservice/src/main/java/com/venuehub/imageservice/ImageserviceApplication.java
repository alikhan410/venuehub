package com.venuehub.imageservice;

import com.venuehub.imageservice.entity.Image;
import com.venuehub.imageservice.repository.ImageRepository;
import org.apache.tomcat.util.digester.ArrayStack;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ResourceLoader;

import java.util.*;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.venuehub.imageservice", "com.venuehub.broker"})
//@EnableCaching
public class ImageserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageserviceApplication.class, args);
    }

    @Bean
    CommandLineRunner run(ImageRepository imageRepository) {
        return args -> {
            if (!imageRepository.findAll().isEmpty()) return;
            String vendorName = "vendor";
            String[] venueNames = {
                    "Emerald Banquet",
                    "Royal Hall",
                    "Blue Sky Banquet",
                    "Greenfield Hall",
                    "Grand Banquet"
            };
            Map<String, String[]> venueFilesMap = new HashMap<>();

            // Populate the map with venue names and file names
            venueFilesMap.put("Emerald Banquet", new String[]{"0-eb", "1-eb", "2-eb"});
            venueFilesMap.put("Royal Hall", new String[]{"0-rh", "1-rh", "2-rh"});
            venueFilesMap.put("Blue Sky Banquet", new String[]{"0-bsb", "1-bsb", "2-bsb"});
            venueFilesMap.put("Greenfield Hall", new String[]{"0-gh", "1-gh", "2-gh"});
            venueFilesMap.put("Grand Banquet", new String[]{"0-gb", "1-gb", "2-gb"});

            for (int i = 0; i < 5; i++) {
                String fileName1 = venueFilesMap.get(venueNames[i])[0];
                String fileName2 = venueFilesMap.get(venueNames[i])[1];
                String fileName3 = venueFilesMap.get(venueNames[i])[2];

                final String replace = venueNames[i].toLowerCase().replace(" ", "-");

                String uri1 = "/images/" + vendorName + "/" + replace + "/" + fileName1;
                String uri2 = "/images/" + vendorName + "/" + replace + "/" + fileName2;
                String uri3 = "/images/" + vendorName + "/" + replace + "/" + fileName3;

                String imagePath1 = String.format("E:\\uploads\\%s\\%s\\%s.jpg", vendorName, replace,fileName1);
                String imagePath2 = String.format("E:\\uploads\\%s\\%s\\%s.jpg", vendorName, replace,fileName2);
                String imagePath3 = String.format("E:\\uploads\\%s\\%s\\%s.jpg", vendorName, replace,fileName3);

                Image image1 = Image.builder()
                        .imagePath(imagePath1)
                        .venueName(venueNames[i])
                        .filename(fileName1)
                        .vendorName(vendorName)
                        .uri(uri1)
                        .build();
                imageRepository.save(image1);

                Image image2 = Image.builder()
                        .imagePath(imagePath2)
                        .venueName(venueNames[i])
                        .filename(fileName2)
                        .vendorName(vendorName)
                        .uri(uri2)
                        .build();
                imageRepository.save(image2);

                Image image3 = Image.builder()
                        .imagePath(imagePath3)
                        .venueName(venueNames[i])
                        .filename(fileName3)
                        .vendorName(vendorName)
                        .uri(uri3)
                        .build();

                imageRepository.saveAll(new ArrayList<>(List.of(image1, image2, image3)));
            }
        };
    }
}

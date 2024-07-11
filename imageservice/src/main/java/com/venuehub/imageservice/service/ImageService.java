package com.venuehub.imageservice.service;

import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.imageservice.entity.Image;
import com.venuehub.imageservice.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ResourceLoader resourceLoader, ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<Image> saveImage(MultipartFile[] files, String venueName, String vendorName) throws IOException {
        int position = 0;
        List<Image> imageList = new ArrayList<>();
        String foo = UUID.randomUUID().toString();
        for (MultipartFile file : files) {
            try {
                String fileName = position + "_" + foo + ".jpg";
                position++;
                // Define your external directory path
                Path externalDir = Paths.get("E:/uploads/" + vendorName + "/" + venueName);
                if (!Files.exists(externalDir)) {
                    Files.createDirectories(externalDir); // Create the directory if it doesn't exist
                }

                // Construct the file path within the external directory
                Path filePath = externalDir.resolve(fileName);

                // Save the file
                try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                    fos.write(file.getBytes());
                }

                String uri = "/images/" + vendorName + "/" + venueName + "/" + fileName;
                Image image = new Image(fileName, filePath.toString(), uri, venueName, vendorName);
                imageList.add(image);
            } catch (IOException ex) {
                logger.error("Failed to save image: {}", ex.getMessage());
                throw new RuntimeException();
            }
        }
        return imageRepository.saveAll(imageList);
    }

    public Resource getImage(String vendorName, String venueName, String fileName) {
        String uri = "/images/" + vendorName + "/" + venueName + "/" + fileName;
        logger.info("retrieving image for the URI: {}", uri);
        try {
            Image image = imageRepository.findByUri(uri).orElseThrow(NoSuchVenueException::new);
            Path path = Paths.get(image.getImagePath());
            return new PathResource(path);

        } catch (NoSuchElementException ex) {
            logger.error("Image not found for URI: {}", uri);
            throw new RuntimeException("Image not found for URI: " + uri);
        }
    }


    public Resource getImage(String vendorName, String venueName) {
        String uri = "/images/" + vendorName + "/" + venueName + "/" + 0;
        logger.info("retrieving image for the partial URI: {}", uri);
        try {
            Image image = imageRepository.findByPartialUri(uri).orElseThrow(NoSuchVenueException::new);
            Path path = Paths.get(image.getImagePath());
            return new PathResource(path);

        } catch (NoSuchElementException ex) {
            logger.error("Image not found for URI: {}", uri);
            throw new RuntimeException("Image not found for URI: " + uri);
        }
    }
}


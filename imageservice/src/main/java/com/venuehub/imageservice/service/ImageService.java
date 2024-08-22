package com.venuehub.imageservice.service;

import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.imageservice.entity.Image;
import com.venuehub.imageservice.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageService {

    @Value("${constants.file-upload-dir}")
    String fileUploadDir;

    @Autowired
    private S3Service s3Service;

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ResourceLoader resourceLoader, ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public List<Image> saveImage(MultipartFile[] files, String venueName, String vendorName) throws IOException {
        int position = 0;

        String input = venueName.replaceAll("-", " "); //Output: venue-name -> venue name
        String formattedVenueName = Arrays.stream(input.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));// Output: venue name -> Venue Name

        List<Image> imageList = new ArrayList<>();
        String foo = UUID.randomUUID().toString();

        for (MultipartFile file : files) {
            try {
                String fileName = position + "-" + foo + ".jpg";
                position++;
                String s3BucketURL = String.format("https://venuehub-imagebucket.s3.eu-north-1.amazonaws.com/%s", fileName);
                String s3Filename = s3Service.uploadFile(file, fileName);
                Image image = Image.builder()
                        .imagePath(s3Filename)
                        .filename(fileName)
                        .uri(s3BucketURL)
                        .vendorName(vendorName)
                        .venueName(formattedVenueName)
                        .build();

                imageList.add(image);
            } catch (IOException ex) {
                logger.error("Failed to save image: {}", ex.getMessage());
                throw new RuntimeException("Failed to save image", ex);
            }
        }
        return imageRepository.saveAll(imageList);
    }

//    public Resource getImage(String vendorName, String venueName, String fileName) {
//        String uri = "/images/" + vendorName + "/" + venueName + "/" + fileName;
//        logger.info("retrieving image for the URI: {}", uri);
//        try {
//            Image image = imageRepository.findByUri(uri).orElseThrow(NoSuchVenueException::new);
//            Path path = Paths.get(image.getImagePath());
//            return new PathResource(path);
//
//        } catch (NoSuchElementException ex) {
//            logger.error("Image not found for URI: {}", uri);
//            throw new RuntimeException("Image not found for URI: " + uri);
//        }
//    }


//    public Resource getImage(String vendorName, String venueName) {
//        String uri = "/images/" + vendorName + "/" + venueName + "/" + 0;
//        logger.info("retrieving image for the partial URI: {}", uri);
//        try {
//            Image image = imageRepository.findByPartialUri(uri).orElseThrow(NoSuchVenueException::new);
//            Path path = Paths.get(image.getImagePath());
//            return new PathResource(path);
//
//        } catch (NoSuchElementException ex) {
//            logger.error("Image not found for URI: {}", uri);
//            throw new RuntimeException("Image not found for URI: " + uri);
//        }
//    }
}


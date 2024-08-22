package com.venuehub.venueservice.service;

import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.venueservice.model.Image;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.repository.ImageRepository;
import com.venuehub.venueservice.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final VenueRepository venueRepository;

    @Autowired
    public ImageService(S3Service s3Service, ImageRepository imageRepository, VenueRepository venueRepository) {
        this.s3Service = s3Service;
        this.imageRepository = imageRepository;
        this.venueRepository = venueRepository;
    }

    @Transactional
    public void saveAll(Iterable<Image> images) {
        imageRepository.saveAll(images);
    }

    @Transactional
    public void saveImage(MultipartFile[] files, long venueId, String vendorName) throws IOException {
        int position = 0;
        Venue venue = venueRepository.findById(venueId).orElseThrow(NoSuchVenueException::new);

        List<Image> imageList = new ArrayList<>();
        String foo = UUID.randomUUID().toString();

        for (MultipartFile file : files) {
            try {
                String fileName = position + "-" + foo + ".jpg";
                position++;
                String s3BucketURL = String.format("https://venuehub-imagebucket.s3.eu-north-1.amazonaws.com/%s", fileName);
                String s3Filename = s3Service.uploadFile(file, fileName);
                Image image = Image.builder()
                        .filename(fileName)
                        .url(s3BucketURL)
                        .vendorName(vendorName)
                        .venueName(venue.getName())
                        .build();

                imageList.add(image);
            } catch (IOException ex) {
                logger.error("Failed to save image: {}", ex.getMessage());
                throw new RuntimeException("Failed to save image", ex);
            }
        }
        List<Image> images = imageRepository.saveAll(imageList);
        List<Image> venueImages = venue.getImages();
        venueImages.addAll(images);
        venueRepository.save(venue);
    }
}



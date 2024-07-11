package com.venuehub.imageservice.controller;

import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.image.ImageCreatedEvent;
import com.venuehub.broker.producer.image.ImageCreatedProducer;
import com.venuehub.imageservice.entity.Image;
import com.venuehub.imageservice.service.ImageService;
import com.venuehub.imageservice.utils.SecurityChecks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class ImageController implements ImageApi {
    public static final Logger logger = LoggerFactory.getLogger(ImageController.class);
    private final ImageService imageService;
    private final ImageCreatedProducer producer;

    public ImageController(ImageService imageService, ImageCreatedProducer producer) {
        this.imageService = imageService;
        this.producer = producer;
    }

    @GetMapping(value = "/images/{vendorName}/{venueName}/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getImage(
            @PathVariable("venueName") String venueName,
            @PathVariable("vendorName") String vendorName,
            @PathVariable("fileName") String fileName
    ) {
        Resource resource = imageService.getImage(vendorName, venueName, fileName);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @GetMapping(value = "/images/{vendorName}/{venueName}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getMainImage(@PathVariable("venueName") String venueName, @PathVariable("vendorName") String vendorName) {
        Resource resource = imageService.getImage(vendorName, venueName);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PostMapping("/images/{venueName}")
    public ResponseEntity<HttpStatus> saveImage(@PathVariable("venueName") String venueName, MultipartFile[] files, @AuthenticationPrincipal Jwt jwt) throws IOException {
        SecurityChecks.vendorCheck(jwt);
        List<Image> images = imageService.saveImage(files, venueName, jwt.getSubject());

        for (Image image : images) {
            ImageCreatedEvent event = new ImageCreatedEvent(image.getId(), image.getUri(), venueName, image.getVendorName());
            producer.produce(event, MyExchange.VENUE_EXCHANGE);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

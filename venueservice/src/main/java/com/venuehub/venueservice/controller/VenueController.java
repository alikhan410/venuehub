package com.venuehub.venueservice.controller;


import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.venue.VenueCreatedEvent;
import com.venuehub.broker.event.venue.VenueDeletedEvent;
import com.venuehub.broker.event.venue.VenueUpdatedEvent;
import com.venuehub.broker.producer.venue.VenueCreatedProducer;
import com.venuehub.broker.producer.venue.VenueDeletedProducer;
import com.venuehub.broker.producer.venue.VenueUpdatedProducer;
import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.commons.exception.UserForbiddenException;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.mapper.Mapper;
import com.venuehub.venueservice.model.BookedVenue;
import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.response.VenueListResponse;
import com.venuehub.venueservice.service.ImageDataService;
import com.venuehub.venueservice.service.VenueService;
import jakarta.servlet.annotation.MultipartConfig;
import jdk.jfr.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class VenueController {

    private final Logger LOGGER = LoggerFactory.getLogger(VenueController.class);

    private final VenueService venueService;
    private final ImageDataService imageDataService;
    private final VenueCreatedProducer venueCreatedProducer;
    private final VenueDeletedProducer venueDeletedProducer;
    private final VenueUpdatedProducer venueUpdatedProducer;


    @Autowired
    public VenueController(VenueService venueService, ImageDataService imageDataService, VenueCreatedProducer venueCreatedProducer, VenueDeletedProducer venueDeletedProducer, VenueUpdatedProducer venueUpdatedProducer) {
        this.venueService = venueService;
        this.imageDataService = imageDataService;
        this.venueCreatedProducer = venueCreatedProducer;
        this.venueDeletedProducer = venueDeletedProducer;
        this.venueUpdatedProducer = venueUpdatedProducer;
    }

    @GetMapping("/venue/{id}")
    public ResponseEntity<VenueDto> getVenueById(@PathVariable Long id) {
        Venue venue = venueService.findById(id).orElseThrow(NoSuchVenueException::new);
        VenueDto venueDto = Mapper.modelToDto(venue);
        return new ResponseEntity<>(venueDto, HttpStatus.OK);
    }

    @PostMapping(value = "/venue", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<VenueDto> addVenue(@ModelAttribute VenueDto body, @RequestParam("images") MultipartFile[] images, @AuthenticationPrincipal Jwt jwt) throws IOException {

        if (!jwt.getClaimAsStringList("roles").contains("VENDOR")) {
            throw new UserForbiddenException();
        }

        List<ImageData> allImages = new ArrayList<>();

        List<BookedVenue> bookings = new ArrayList<>();
        Venue newVenue = Venue.builder()
                .venueType(body.venueType())
                .username(jwt.getSubject())
                .images(allImages)
                .phone(body.phone())
                .name(body.name())
                .location(body.location())
                .estimate(body.estimate())
                .bookings(bookings)
                .capacity(body.capacity())
                .build();
        venueService.save(newVenue);

        for (MultipartFile image : images) {
            ImageData imageData = new ImageData();
            imageData.setImage(image.getBytes());
            imageData.setVenue(newVenue);
            imageDataService.save(imageData);
//            allImages.add(imageData);
        }

//        newVenue.setImages(allImages);
//        venueService.save(newVenue);
        LOGGER.info("Venue added");

        VenueDto venueDto = Mapper.modelToDto(newVenue);

        //Sending venue created event to the broker
        VenueCreatedEvent event = new VenueCreatedEvent(newVenue.getId(), jwt.getSubject());
        venueCreatedProducer.produce(event, MyExchange.BOOKING_EXCHANGE);

        return new ResponseEntity<>(venueDto, HttpStatus.CREATED);
    }


    @DeleteMapping("/venue/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> deleteVenue(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {

        //checking if a venue exists
        Venue venue = venueService.findById(id).orElseThrow(NoSuchVenueException::new);

        if (!jwt.getSubject().equals(venue.getUsername()) || !jwt.getClaimAsStringList("roles").contains("VENDOR")) {
            throw new UserForbiddenException();
        }

        venueService.deleteById(id);

        //Sending venue deleted event to the broker
        VenueDeletedEvent event = new VenueDeletedEvent(id, jwt.getSubject());
        venueDeletedProducer.produce(event, MyExchange.BOOKING_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/venue")
    public ResponseEntity<VenueListResponse> getVenue() {

        List<Venue> venueList = venueService.findAll();

        List<VenueDto> venueDtoList = venueList.stream().map(Mapper::modelToDto).toList();

        VenueListResponse response = new VenueListResponse(venueDtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/venue/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> updateVenue(@PathVariable long id, @RequestBody VenueDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {

        Venue venue = venueService.findById(id).orElseThrow(NoSuchVenueException::new);
        if (!jwt.getSubject().equals(venue.getUsername()) || !jwt.getClaimAsStringList("roles").contains("VENDOR")) {
            throw new UserForbiddenException();
        }
        venue.setName(body.name());
        venue.setCapacity(body.capacity());
        venue.setPhone(body.phone());
        venue.setEstimate(body.estimate());

        venueService.save(venue);

        VenueUpdatedEvent event = new VenueUpdatedEvent(
                id,
                body.name(),
                body.capacity(),
                body.phone(),
                body.estimate()
        );
        venueUpdatedProducer.produce(event, MyExchange.BOOKING_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

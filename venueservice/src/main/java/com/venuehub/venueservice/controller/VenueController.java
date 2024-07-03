package com.venuehub.venueservice.controller;


import com.venuehub.broker.constants.BookingStatus;
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
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.response.MainVenueImage;
import com.venuehub.venueservice.response.VenueAddedResponse;
import com.venuehub.venueservice.response.VenueListResponse;
import com.venuehub.venueservice.service.ImageDataService;
import com.venuehub.venueservice.service.VenueService;
import com.venuehub.venueservice.utils.SecurityChecks;
import jakarta.validation.Valid;
import org.apache.commons.io.file.FilesUncheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
public class VenueController {
    private final Logger logger = LoggerFactory.getLogger(VenueController.class);

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
        logger.info("Received request to get venue with id: {}", id);
        VenueDto venue = venueService.loadVenueDtoById(id);

        //Redundant - Just to make the test pass
        if (venue == null) throw new NoSuchVenueException();

        logger.info("Returning venue details for id: {}", venue);
        return new ResponseEntity<>(venue, HttpStatus.OK);
    }

    @GetMapping("/venue/{venueId}/image-0")
    public ResponseEntity<MainVenueImage> getMainImage(@PathVariable Long venueId) {
        logger.info("Received request to get main image for venue with id: {}", venueId);
        Venue venue = venueService.findById(venueId).orElseThrow(() -> {
            logger.error("Venue not found with id: {}", venueId);
            return new NoSuchVenueException();
        });
        ImageData mainImage = venue.getImages().get(0);
        MainVenueImage res = new MainVenueImage(mainImage);
        logger.info("Returning main image for venue id: {}", venueId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(value = "/venue", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<VenueAddedResponse> addVenue(@ModelAttribute @Valid VenueDto body, @RequestParam("images") MultipartFile[] images, @AuthenticationPrincipal Jwt jwt) throws IOException {
        logger.info("Received request to add a new venue by user: {}", jwt.getSubject());

        SecurityChecks.vendorCheck(jwt);

        //Creating temp files for storing the images to prevent
        //multiple threads from using the same resources which is in this case
        //is MultiPartFile[] images
        List<Path> pathList = new ArrayList<>();
        for (MultipartFile image : images) {
            Path tempPath = FilesUncheck.createTempFile("venue_image_", ".tmp");
            image.transferTo(tempPath);
            pathList.add(tempPath);
        }

        Venue newVenue = venueService.buildVenue(body, jwt.getSubject());
        venueService.save(newVenue);

        //storing the images asynchronously
        //it runs in the same transactional context maintaining data integrity
        imageDataService.storeImagesAsync(newVenue, pathList);

        //storing the images synchronously
//        imageDataService.storeImagesSync(newVenue, images);

        logger.info("Venue added with id: {} by user: {}", newVenue.getId(), jwt.getSubject());

        //Sending venue created event to the broker
        VenueCreatedEvent event = new VenueCreatedEvent(
                newVenue.getId(),
                newVenue.getName(),
                newVenue.getEstimate(),
                jwt.getSubject()
        );
        venueCreatedProducer.produce(event, MyExchange.BOOKING_EXCHANGE);

        VenueAddedResponse response = new VenueAddedResponse();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/venue/{id}")
    @Transactional
    public ResponseEntity<VenueListResponse> deleteVenue(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {
        logger.info("Received request to delete venue with id: {} by user: {}", id, jwt.getSubject());

        SecurityChecks.vendorCheck(jwt);

        //checking if a venue exists
        Venue venue = venueService.findById(id).orElseThrow(() -> {
            logger.error("Venue not found with id: {}", id);
            return new NoSuchVenueException();
        });

        List<Booking> bookings = venue.getBookings().stream().filter(booking -> booking.getStatus() != BookingStatus.FAILED).toList();

        //If user does not exist or user role does not contain vendor or Venue does not have any bookings
        if (!jwt.getSubject().equals(venue.getUsername()) || !bookings.isEmpty()) {
            logger.warn("User: {} is not authorized to delete venue id: {} or venue has active bookings", jwt.getSubject(), id);
            throw new UserForbiddenException();
        }

        venueService.delete(venue);
        logger.info("Venue with id: {} deleted by user: {}", id, jwt.getSubject());

        //Sending venue deleted event to the broker
        VenueDeletedEvent event = new VenueDeletedEvent(id, jwt.getSubject());
        venueDeletedProducer.produce(event, MyExchange.BOOKING_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/venue")
    public ResponseEntity<VenueListResponse> getVenue() {
        logger.info("Received request to get all venues");
        List<VenueDto> venues = venueService.loadAllVenues();
        VenueListResponse response = new VenueListResponse(venues);
        logger.info("Returning list of all venues");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/venue/vendor/all-venue")
    public ResponseEntity<VenueListResponse> getVenueByVendorName(@AuthenticationPrincipal Jwt jwt) {

        logger.info("Received request to get all venues for vendor: {}", jwt.getSubject());
        SecurityChecks.vendorCheck(jwt);

        List<Venue> venueList = venueService.findByUsername(jwt.getSubject());
        List<VenueDto> venueDtoList = venueList.stream().map(Mapper::modelToVenueDto).toList();

        VenueListResponse response = new VenueListResponse(venueDtoList);
        logger.info("Returning list of venues for vendor: {}", jwt.getSubject());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/venue/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> updateVenue(@PathVariable long id, @RequestBody VenueDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {
        logger.info("Received request to update venue with id: {} by user: {}", id, jwt.getSubject());
        SecurityChecks.vendorCheck(jwt);

        Venue venue = venueService.findById(id).orElseThrow(() -> {
            logger.error("Venue not found with id: {}", id);
            return new NoSuchVenueException();
        });

        if (!jwt.getSubject().equals(venue.getUsername())) {
            logger.warn("User: {} is not authorized to update venue id: {}", jwt.getSubject(), id);
            throw new UserForbiddenException();
        }
        venue.setName(body.name());
        venue.setDescription(body.description());
        venue.setCapacity(Integer.parseInt(body.capacity()));
        venue.setPhone(body.phone());
        venue.setEstimate(Integer.parseInt(body.estimate()));

        venueService.save(venue);
        logger.info("Venue with id: {} updated by user: {}", id, jwt.getSubject());

        VenueUpdatedEvent event = new VenueUpdatedEvent(
                id,
                body.name(),
                Integer.parseInt(body.capacity()),
                body.phone(),
                body.estimate()
        );
        venueUpdatedProducer.produce(event, MyExchange.BOOKING_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

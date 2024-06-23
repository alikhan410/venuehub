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
import com.venuehub.venueservice.dto.VenueListDto;
import com.venuehub.venueservice.mapper.Mapper;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.response.MainVenueImage;
import com.venuehub.venueservice.response.VenueAddedResponse;
import com.venuehub.venueservice.response.VenueListResponse;
import com.venuehub.venueservice.service.ImageDataService;
import com.venuehub.venueservice.service.VenueService;
import jakarta.validation.Valid;
import org.apache.commons.io.file.FilesUncheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        VenueDto venueDto = Mapper.modelToVenueDto(venue);
        return new ResponseEntity<>(venueDto, HttpStatus.OK);
    }

    @GetMapping("/venue/{id}/image-0")
    public ResponseEntity<MainVenueImage> getMainImage(@PathVariable Long venueId) {
        Venue venue = venueService.findById(venueId).orElseThrow(NoSuchVenueException::new);
        ImageData mainImage = venue.getImages().get(0);
        MainVenueImage res = new MainVenueImage(mainImage);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(value = "/venue", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<VenueAddedResponse> addVenue(@ModelAttribute @Valid VenueDto body, @RequestParam("images") MultipartFile[] images, @AuthenticationPrincipal Jwt jwt) throws IOException {

        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }

        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("VENDOR")) {
            throw new UserForbiddenException();
        }

        //Creating temp files for storing the images to prevent
        //multiple threads from using the same resources which is in this case
        //is MultiPartFile[] images
        List<Path> pathList = new ArrayList<>();
        for (MultipartFile image : images) {
            Path tempPath = FilesUncheck.createTempFile("venue_image_", ".tmp");
            image.transferTo(tempPath);
            pathList.add(tempPath);
        }

        List<ImageData> allImages = new ArrayList<>();

        List<Booking> bookings = new ArrayList<>();
        Venue newVenue = Venue.builder()
                .venueType(body.venueType())
                .username(jwt.getSubject())
                .images(allImages)
                .phone(body.phone())
                .name(body.name())
                .description(body.description())
                .location(body.location())
                .estimate(Integer.parseInt(body.estimate()))
                .bookings(bookings)
                .capacity(Integer.parseInt(body.capacity()))
                .build();

        venueService.save(newVenue);

        //storing the images asynchronously
        //it runs in the same transactional context maintaining data integrity
        imageDataService.storeImagesAsync(newVenue, pathList);

        //storing the images synchronously
//        imageDataService.storeImagesSync(newVenue, images);

        LOGGER.info("Venue added");

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

        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }

        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        //checking if a venue exists
        Venue venue = venueService.findById(id).orElseThrow(NoSuchVenueException::new);
        List<Booking> bookings = venue.getBookings().stream().filter(booking -> booking.getStatus() != BookingStatus.FAILED).toList();
        if (
                !jwt.getSubject().equals(venue.getUsername()) ||
                        !roles.contains("VENDOR") ||
                        !bookings.isEmpty()
        ) {
            throw new UserForbiddenException();
        }


        venueService.delete(venue);

        //Sending venue deleted event to the broker
        VenueDeletedEvent event = new VenueDeletedEvent(id, jwt.getSubject());
        venueDeletedProducer.produce(event, MyExchange.BOOKING_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/venue")
    public ResponseEntity<VenueListResponse> getVenue() {

        VenueListResponse response = venueService.getAllVenues();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/venue/vendor/all-venue")
    public ResponseEntity<VenueListResponse> getVenueByVendorName(@AuthenticationPrincipal Jwt jwt) {

        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }

        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("VENDOR")) {
            throw new UserForbiddenException();
        }

        List<Venue> venueList = venueService.findByUsername(jwt.getSubject());

        List<VenueListDto> venueDtoList = venueList.stream().map(Mapper::modelToVenueListDto).toList();

        VenueListResponse response = new VenueListResponse(venueDtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/venue/{id}")
    @Transactional
    public ResponseEntity<HttpStatus> updateVenue(@PathVariable long id, @RequestBody VenueDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {

        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }

        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        Venue venue = venueService.findById(id).orElseThrow(NoSuchVenueException::new);
        if (!jwt.getSubject().equals(venue.getUsername()) || !roles.contains("VENDOR")) {
            throw new UserForbiddenException();
        }
        venue.setName(body.name());
        venue.setDescription(body.description());
        venue.setCapacity(Integer.parseInt(body.capacity()));
        venue.setPhone(body.phone());
        venue.setEstimate(Integer.parseInt(body.estimate()));

        venueService.save(venue);

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

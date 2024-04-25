package com.venuehub.venueservice.controller;


import com.venuehub.broker.event.venue.VenueCreatedEvent;
import com.venuehub.broker.event.venue.VenueDeletedEvent;
import com.venuehub.broker.producer.VenueCreatedProducer;
import com.venuehub.broker.producer.VenueDeletedProducer;
import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.commons.exception.UserUnAuthorizedException;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.mapper.Mapper;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.response.VenueListResponse;
import com.venuehub.venueservice.service.VenueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@Validated
public class VenueController {

    private final Logger LOGGER = LoggerFactory.getLogger(VenueController.class);

    private final VenueService venueService;
    private final VenueCreatedProducer venueCreatedProducer;
    private final VenueDeletedProducer venueDeletedProducer;

    @Autowired
    public VenueController(VenueService venueService, VenueCreatedProducer venueCreatedProducer, VenueDeletedProducer venueDeletedProducer) {
        this.venueService = venueService;
        this.venueCreatedProducer = venueCreatedProducer;
        this.venueDeletedProducer = venueDeletedProducer;
    }

    @GetMapping("/venue/{id}")
    public ResponseEntity<VenueDto> getVenueById(@PathVariable Long id) {
        Venue venue = venueService.findById(id).orElseThrow(NoSuchVenueException::new);
        VenueDto venueDto = Mapper.modelToDto(venue);
        return new ResponseEntity<>(venueDto, HttpStatus.OK);
    }

    @PostMapping("/venue")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<VenueDto> addVenue(@RequestBody VenueDto body, @AuthenticationPrincipal Jwt jwt) {

        Venue newVenue = new Venue();

        newVenue.setName(body.name());
        newVenue.setVenueType(body.venueType());
        newVenue.setEstimate(body.estimate());
        newVenue.setCapacity(body.capacity());
        newVenue.setPhone(body.phone());
        newVenue.setLocation(body.location());
        newVenue.setUsername(jwt.getSubject());

        venueService.save(newVenue);
        LOGGER.info("Venue added");

        VenueDto venueDto = Mapper.modelToDto(newVenue);

        //Sending venue created event to the broker
        VenueCreatedEvent event = new VenueCreatedEvent(newVenue.getId(), jwt.getSubject());
        venueCreatedProducer.produce(event);

        return new ResponseEntity<>(venueDto, HttpStatus.CREATED);
    }


    @DeleteMapping("/venue/{id}")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<HttpStatus> deleteVenue(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {

        //checking if a venue exists
        Venue venue = venueService.findById(id).orElseThrow(NoSuchVenueException::new);

        if (!jwt.getSubject().equals(jwt.getSubject())) {
            throw new UserUnAuthorizedException();
        }

        venueService.deleteById(id);

        //Sending venue deleted event to the broker
        VenueDeletedEvent event = new VenueDeletedEvent(id, jwt.getSubject());
        venueDeletedProducer.produce(event);

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
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<HttpStatus> updateVenue(@PathVariable long id, VenueDto body,@AuthenticationPrincipal Jwt jwt) throws Exception {

        Venue venue = venueService.findById(id).orElseThrow(NoSuchVenueException::new);
        if (jwt.getSubject()!= venue.getUsername()) {
            throw new UserUnAuthorizedException();
        }

        venue.setEstimate(body.estimate());
        venue.setCapacity(body.capacity());
        venue.setPhone(body.phone());

        venueService.save(venue);

        //TODO produce venue updated event

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

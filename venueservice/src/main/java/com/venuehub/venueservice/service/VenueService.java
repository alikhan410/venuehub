package com.venuehub.venueservice.service;

import com.venuehub.commons.exception.NoSuchVenueException;
import com.venuehub.commons.exception.UserForbiddenException;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.mapper.Mapper;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.repository.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class VenueService {
    public static final Logger logger = LoggerFactory.getLogger(VenueService.class);

    private final VenueRepository venueRepository;

    @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Transactional
    @CacheEvict(value = "venue:all", allEntries = true)
    public void save(Venue venue) {
        venueRepository.save(venue);
    }

    @Transactional
    @CacheEvict(value = "venue:all", allEntries = true)
    public void saveAll(List<Venue> venues) {
        venueRepository.saveAll(venues);
    }

    public Optional<Venue> findById(long id) {
        logger.info("Fetching the venue associated with the id: {}", id);
        return venueRepository.findById(id);
    }

    @Cacheable(value = "venue:id", key = "#id")
    public VenueDto loadVenueDtoById(long id) {
        logger.info("Loading the venue associated with the id: {}", id);
        Venue venue = venueRepository.findById(id).orElseThrow(NoSuchVenueException::new);
        return Mapper.modelToVenueDto(venue);

    }

    public List<Venue> findByUsername(String username) {
        return venueRepository.findVenueByUsername(username);
    }

    @Transactional
    @CacheEvict(value = "venue:all")
    public void delete(Venue venue) {
        venueRepository.delete(venue);
    }

    public List<Venue> findAll() {
        logger.info("Fetching all venues");
        return venueRepository.findAll();
    }

    @Cacheable("venue:all")
    public List<VenueDto> loadAllVenues() {
        logger.info("Loading all venues");
        List<Venue> venues = venueRepository.findAll();
        return venues.stream().map(Mapper::modelToVenueDto).toList();
    }

    public Venue buildVenue(VenueDto body, String username) {
        List<ImageData> allImages = new ArrayList<>();

        List<Booking> bookings = new ArrayList<>();

        return Venue.builder()
                .venueType(body.venueType())
                .username(username)
                .images(allImages)
                .phone(body.phone())
                .name(body.name())
                .description(body.description())
                .location(body.location())
                .estimate(Integer.parseInt(body.estimate()))
                .bookings(bookings)
                .capacity(Integer.parseInt(body.capacity()))
                .build();

    }

    public void vendorChecks(Jwt jwt) {

        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("VENDOR") || !jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }
    }
}

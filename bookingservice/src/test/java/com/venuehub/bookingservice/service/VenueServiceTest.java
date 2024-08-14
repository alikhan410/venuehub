package com.venuehub.bookingservice.service;

import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.repository.VenueRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VenueServiceTest {
    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private VenueService venueService;

    Venue venue;

    @BeforeEach
    void beforeEachSetUp() {
        venueRepository.deleteAll();
        venue = new Venue(1L, "Emerald Banquet", 75000, "vendor");
        venueRepository.save(venue);
    }
    @AfterEach
    void afterEachSetUp(){
        venueRepository.deleteAll();
    }

    @Test
    void save() {
        Venue newVenue = new Venue(2L, "Blue Sky Banquet", 50000, "vendor");
        venueRepository.save(newVenue);

        Optional<Venue> savedVenue = venueRepository.findById(newVenue.getId());
        assertTrue(savedVenue.isPresent());
        assertEquals("Blue Sky Banquet", savedVenue.get().getName());
    }

    @Test
    void findById() {
        Optional<Venue> savedVenue = venueRepository.findById(venue.getId());
        assertTrue(savedVenue.isPresent());
        assertEquals("Emerald Banquet", savedVenue.get().getName());
    }

    @Test
    void findByUsername() {
        List<Venue> foundVenues = venueRepository.findByUsername("vendor");
        assertEquals(1, foundVenues.size());
        assertEquals("Emerald Banquet", foundVenues.get(0).getName());

    }

}
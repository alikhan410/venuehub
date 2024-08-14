package com.venuehub.venueservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.repository.VenueRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class VenueServiceTest {
    @Autowired
    private VenueService venueService;

    @Autowired
    private VenueRepository venueRepository;

    private Venue venue;

    @BeforeEach
    public void beforeEachSetUp() {
        venue = Venue.builder()
                .venueType("Banquet")
                .phone("03001234567")
                .capacity(300)
                .description("A large banquet suitable for corporate events.")
                .estimate(75000)
                .username("vendor")
                .name("Emerald Banquet")
                .location("Clifton, Karachi")
                .build();
        venueRepository.save(venue);
    }
    @AfterEach
    public void afterEachSetup(){
        venueRepository.deleteAll();
    }

    @Test
    void save() {
        Venue newVenue = Venue.builder()
                .venueType("Hall")
                .phone("03119876543")
                .capacity(500)
                .description("A grand hall with elegant decor.")
                .estimate(150000)
                .username("vendor")
                .name("Royal Hall")
                .location("DHA Phase 5, Karachi")
                .build();
        venueService.save(newVenue);

        Optional<Venue> savedVenue = venueRepository.findById(newVenue.getId());
        assertThat(savedVenue).isPresent();
        assertThat(savedVenue.get().getVenueType()).isEqualTo("Hall");
        assertThat(savedVenue.get().getPhone()).isEqualTo("03119876543");
        assertThat(savedVenue.get().getCapacity()).isEqualTo(500);
        assertThat(savedVenue.get().getDescription()).isEqualTo("A grand hall with elegant decor.");
        assertThat(savedVenue.get().getEstimate()).isEqualTo(150000);
        assertThat(savedVenue.get().getUsername()).isEqualTo("vendor");
        assertThat(savedVenue.get().getName()).isEqualTo("Royal Hall");
        assertThat(savedVenue.get().getLocation()).isEqualTo("DHA Phase 5, Karachi");

    }

    @Test
    void findById() {
        Optional<Venue> foundVenue = venueService.findById(venue.getId());
        assertThat(foundVenue).isPresent();
        assertThat(foundVenue.get().getName()).isEqualTo("Emerald Banquet");
    }

    @Test
    void findByUsername() {
        List<VenueDto> venues = venueService.findByUsername("vendor");
        assertThat(venues).isNotEmpty();
        assertThat(venues.get(0).username()).isEqualTo("vendor");
    }

    @Test
    void delete() {
        venueService.delete(venue);
        Optional<Venue> foundVenue = venueRepository.findById(venue.getId());
        assertThat(foundVenue).isNotPresent();
    }

    @Test
    void findAll() {
        List<Venue> venues = venueService.findAll();
        assertThat(venues).isNotEmpty();
    }

}
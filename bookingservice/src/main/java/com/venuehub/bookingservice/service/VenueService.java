package com.venuehub.bookingservice.service;

import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.repository.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void save(Venue venue) {
        venueRepository.save(venue);
    }

    @Transactional
    public void saveAll(List<Venue> venues) {
        venueRepository.saveAll(venues);
    }

    public Optional<Venue> findById(long id) {
        return venueRepository.findById(id);
    }

    public List<Venue> findByUsername(String username) {
        return venueRepository.findByUsername(username);
    }

    @Transactional
    public void deleteById(long id) {
        venueRepository.deleteById(id);
    }

    public List<Venue> findAll() {
        return venueRepository.findAll();
    }
}

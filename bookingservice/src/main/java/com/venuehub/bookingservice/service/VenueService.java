package com.venuehub.bookingservice.service;

import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VenueService {
    private final VenueRepository venueRepository;

    @Autowired
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Transactional
    public void save(Venue venue) {
        venueRepository.save(venue);
    }

    public Optional<Venue> findById(long id) {
        return venueRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id){
        venueRepository.deleteById(id);
    }

    public List<Venue> findAll(){
        return venueRepository.findAll();
    }
}

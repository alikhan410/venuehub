package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.repository.VenueRepository;
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

    public List<Venue> findByUsername(String username){
        return venueRepository.findVenueByUsername(username);
    }

    @Transactional
    public void delete(Venue venue){
        venueRepository.delete(venue);
    }

//    public Long getUserId(Long venueId){
//       return venueRepository.getVendorId(venueId);
//    }
    public List<Venue> findAll(){
        return venueRepository.findAll();
    }
}

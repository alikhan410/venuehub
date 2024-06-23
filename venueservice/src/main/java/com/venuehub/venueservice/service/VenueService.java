package com.venuehub.venueservice.service;

import com.venuehub.venueservice.dto.VenueListDto;
import com.venuehub.venueservice.mapper.Mapper;
import com.venuehub.venueservice.model.Venue;
import com.venuehub.venueservice.repository.VenueRepository;
import com.venuehub.venueservice.response.VenueListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
        return venueRepository.findById(id);
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
        return venueRepository.findAll();
    }

    @Cacheable("venue:all")
    public VenueListResponse getAllVenues() {

        List<Venue> venueList = venueRepository.findAll();

        List<VenueListDto> venueDtoList = venueList.stream().map(Mapper::modelToVenueListDto).toList();

        return new VenueListResponse(venueDtoList);
    }

}

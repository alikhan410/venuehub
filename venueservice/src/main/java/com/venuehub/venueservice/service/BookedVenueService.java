package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.BookedVenue;
import com.venuehub.venueservice.repository.BookedVenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class BookedVenueService {
    private final BookedVenueRepository bookedVenueRepository;

    @Autowired
    public BookedVenueService(BookedVenueRepository bookedVenueRepository) {
        this.bookedVenueRepository = bookedVenueRepository;
    }

    @Transactional
    public void save(BookedVenue bookedVenue) {
        bookedVenueRepository.save(bookedVenue);
    }

    public Optional<BookedVenue> findById(Long id) {
        return bookedVenueRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id) {
        bookedVenueRepository.deleteById(id);
    }

    public List<BookedVenue> findByVenue(long id) {
        return bookedVenueRepository.findByVenue(id);
    }

}

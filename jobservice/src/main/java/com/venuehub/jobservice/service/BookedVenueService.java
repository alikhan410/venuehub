package com.venuehub.jobservice.service;

import com.venuehub.jobservice.entity.BookedVenue;
import com.venuehub.jobservice.repository.BookedVenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}

package com.venuehub.jobservice.service;

import com.venuehub.jobservice.entity.Booking;
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
    public void save(Booking booking) {
        bookedVenueRepository.save(booking);
    }

    public Optional<Booking> findById(Long id) {
        return bookedVenueRepository.findById(id);
    }

}

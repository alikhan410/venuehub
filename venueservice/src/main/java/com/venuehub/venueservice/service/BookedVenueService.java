package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.Booking;
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
    public void save(Booking booking) {
        bookedVenueRepository.save(booking);
    }

    public Optional<Booking> findById(Long id) {
        return bookedVenueRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id) {
        bookedVenueRepository.deleteById(id);
    }

    public List<Booking> findByVenue(long id) {
        return bookedVenueRepository.findByVenue(id);
    }

}

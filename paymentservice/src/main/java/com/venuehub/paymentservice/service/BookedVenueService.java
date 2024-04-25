package com.venuehub.paymentservice.service;

import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.paymentservice.model.BookedVenue;
import com.venuehub.paymentservice.repository.BookedVenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BookedVenueService {
    private final BookedVenueRepository bookedVenueRepository;

    public BookedVenueService(BookedVenueRepository bookedVenueRepository) {
        this.bookedVenueRepository = bookedVenueRepository;
    }

    @Transactional
    public void save(BookedVenue bookedVenue) {
        bookedVenueRepository.save(bookedVenue);
    }

    public Optional<BookedVenue> findById(Long id){
        return bookedVenueRepository.findById(id);
    }
    @Transactional
    public void updateStatus(Long id, BookingStatus status) throws Exception {
        BookedVenue booking = bookedVenueRepository.findById(id).orElseThrow(() -> new Exception("No Such Booking"));
        booking.setStatus(status);
        bookedVenueRepository.save(booking);
    }
}

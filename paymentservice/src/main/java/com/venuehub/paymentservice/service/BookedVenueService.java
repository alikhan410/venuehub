package com.venuehub.paymentservice.service;

import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.paymentservice.model.Booking;
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
    public void save(Booking booking) {
        bookedVenueRepository.save(booking);
    }

    public Optional<Booking> findById(Long id){
        return bookedVenueRepository.findById(id);
    }
    @Transactional
    public void updateStatus(Long id, BookingStatus status) throws Exception {
        Booking booking = bookedVenueRepository.findById(id).orElseThrow(() -> new Exception("No Such Booking"));
        booking.setStatus(status);
        bookedVenueRepository.save(booking);
    }
}

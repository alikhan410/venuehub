package com.venuehub.venueservice.service;

import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Transactional
    public void deleteById(long id) {
        bookingRepository.deleteById(id);
    }

    public List<Booking> findByVenue(long id) {
        return bookingRepository.findByVenue(id);
    }

}

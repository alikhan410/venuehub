package com.venuehub.paymentservice.service;

import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.paymentservice.model.Booking;
import com.venuehub.paymentservice.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    public Optional<Booking> findById(Long id){
        return bookingRepository.findById(id);
    }
    @Transactional
    public void updateStatus(Long id, BookingStatus status) throws Exception {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new Exception("No Such Booking"));
        booking.setStatus(status);
        bookingRepository.save(booking);
    }
}

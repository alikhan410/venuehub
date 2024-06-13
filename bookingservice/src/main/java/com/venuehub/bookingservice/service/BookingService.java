package com.venuehub.bookingservice.service;

import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.bookingservice.repository.BookedVenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {
    private final BookedVenueRepository bookedVenueRepository;

    @Autowired
    public BookingService(BookedVenueRepository bookedVenueRepository) {
        this.bookedVenueRepository = bookedVenueRepository;
    }

    @Transactional
    public void save(Booking booking) {
        bookedVenueRepository.save(booking);
    }

    public List<Booking> findByVenue(long id) {
        return bookedVenueRepository.findByVenue(id);
    }

    public Boolean isBookingAvailable(LocalDateTime bookingDateTime, List<Booking> bookings) {

        LocalDate bookingDate = bookingDateTime.toLocalDate();
        LocalDate currentDate = LocalDate.now();

        for (Booking booking : bookings) {

            LocalDate currentBooking = LocalDateTime.parse(booking.getBookingDateTime()).toLocalDate();

            if (!bookingDate.isAfter(currentDate) || bookingDate.equals(currentBooking) || bookingDate.equals(currentDate)) {
                return false;
            }

        }
        return true;
    }

    public List<Booking> removeBooking(List<Booking> bookings, long bookingId) {
        ArrayList<Booking> updatedBookings = new ArrayList<>(bookings);

        Iterator<Booking> iterator = updatedBookings.iterator();

        while (iterator.hasNext()) {
            Booking booking = iterator.next();
            if (booking.getId() == bookingId) {
                iterator.remove();
                return updatedBookings;
            }
        }
        return updatedBookings;
    }

    public Optional<Booking> findById(long id) {
        return bookedVenueRepository.findById(id);
    }

    public List<Booking> findByUsername(String username) {
        return bookedVenueRepository.findByUsername(username);
    }

    public Optional<Booking> findCompletedBookingById(long id) {
        return bookedVenueRepository.findCompletedBookingById(id);
    }

    @Transactional
    public void deleteBooking(long id) {
        bookedVenueRepository.deleteById(id);
    }

    @Transactional
    public void updateStatus(long id, BookingStatus status) throws NoSuchBookingException {
        Booking booking = bookedVenueRepository.findById(id).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(status);
        bookedVenueRepository.save(booking);
    }

    @Transactional
    public Booking addNewBooking(BookingDto body, Venue venue, String username) {
        Booking newBooking = Booking.builder()
                .bookingDateTime(body.bookingDateTime())
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(venue.getEstimate())
                .username(username)
                .phone(body.phone())
                .email(body.email())
                .guests(body.guests()).build();
        bookedVenueRepository.save(newBooking);
        return newBooking;
    }

    @Transactional
    public void updateBooking(Booking booking, String newBookingDate) {
        booking.setBookingDateTime(newBookingDate);

        //setting a new reservation date
        String newReservation = LocalDateTime.now(ZoneId.of("PLT")).plusMinutes(2).toString();
        booking.setReservationExpiry(newReservation);

        bookedVenueRepository.save(booking);
    }

}

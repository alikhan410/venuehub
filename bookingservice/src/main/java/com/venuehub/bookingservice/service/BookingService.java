package com.venuehub.bookingservice.service;

import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.bookingservice.repository.BookingRepository;
import com.venuehub.commons.exception.UserForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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

    public List<Booking> findByVenue(long id) {
        return bookingRepository.findByVenue(id);
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
        return bookingRepository.findById(id);
    }

    public List<Booking> findByUsername(String username) {
        return bookingRepository.findByUsername(username);
    }

    public Optional<Booking> findCompletedBookingById(long id) {
        return bookingRepository.findCompletedBookingById(id);
    }

    @Transactional
    public void deleteBooking(long id) {
        bookingRepository.deleteById(id);
    }

    @Transactional
    public void updateStatus(long id, BookingStatus status) throws NoSuchBookingException {
        Booking booking = bookingRepository.findById(id).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(status);
        bookingRepository.save(booking);
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
        bookingRepository.save(newBooking);
        return newBooking;
    }

    @Transactional
    public void updateBooking(Booking booking, String newBookingDate) {
        booking.setBookingDateTime(newBookingDate);

        //setting a new reservation date
        String newReservation = LocalDateTime.now(ZoneId.of("PLT")).plusMinutes(2).toString();
        booking.setReservationExpiry(newReservation);

        bookingRepository.save(booking);
    }

    public void bookingChecks(Jwt jwt){

        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("USER") || !jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException();
        }
    }

}

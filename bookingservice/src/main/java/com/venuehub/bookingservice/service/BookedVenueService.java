package com.venuehub.bookingservice.service;

import com.venuehub.bookingservice.dto.BookedVenueDto;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.bookingservice.repository.BookedVenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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

    public List<BookedVenue> findByVenue(long id) {
        return bookedVenueRepository.findByVenue(id);
    }

    public Boolean isBookingAvailable(LocalDateTime bookingDateTime, List<BookedVenue> bookings) {

        LocalDate bookingDate = bookingDateTime.toLocalDate();
        LocalDate currentDate = LocalDate.now();

        for (BookedVenue booking : bookings) {

            LocalDate currentBooking = LocalDateTime.parse(booking.getBookingDateTime()).toLocalDate();

                if (!bookingDate.isAfter(currentDate) || bookingDate.equals(currentBooking) || bookingDate.equals(currentDate)) {
                    return false;
                }

        }
        return true;
    }

    public List<BookedVenue> removeBooking(List<BookedVenue> bookings, long bookingId) {
        ArrayList<BookedVenue> updatedBookings = new ArrayList<>(bookings);

        Iterator<BookedVenue> iterator = updatedBookings.iterator();

        while (iterator.hasNext()) {
            BookedVenue booking = iterator.next();
            if (booking.getId() == bookingId) {
                iterator.remove();
                return updatedBookings;
            }
        }
        return updatedBookings;
    }

    public Optional<BookedVenue> findById(long id) {
        return bookedVenueRepository.findById(id);
    }

    public Optional<BookedVenue> findCompletedBookingById(long id) {
        return bookedVenueRepository.findCompletedBookingById(id);
    }

    @Transactional
    public void deleteBooking(long id) {
        bookedVenueRepository.deleteById(id);
    }

    @Transactional
    public void updateStatus(long id, BookingStatus status) throws NoSuchBookingException {
        BookedVenue bookedVenue = bookedVenueRepository.findById(id).orElseThrow(NoSuchBookingException::new);
        bookedVenue.setStatus(status);
        bookedVenueRepository.save(bookedVenue);
    }

    @Transactional
    public BookedVenue addNewBooking(BookedVenueDto body, Venue venue, String username){
        BookedVenue newBooking = BookedVenue.builder()
                .bookingDateTime(body.getBookingDateTime())
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .username(username)
                .phone(body.getPhone())
                .email(body.getEmail())
                .guests(body.getGuests()).build();
        bookedVenueRepository.save(newBooking);
        return newBooking;
    }

}

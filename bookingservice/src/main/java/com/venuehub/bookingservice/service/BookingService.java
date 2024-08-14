package com.venuehub.bookingservice.service;

import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.mapper.BookingServiceMapper;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.bookingservice.repository.BookingRepository;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class BookingService {
    public static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final BookingServiceMapper mapper = Mappers.getMapper(BookingServiceMapper.class);

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    @CacheEvict(value = {"booking:id", "booking:complete", "bookings:venueId", "bookings:username", "bookings:datetime"},allEntries = true)
    public void save(Booking booking) {
        bookingRepository.save(booking);
    }

    public List<Booking> findByVenue(long venueId) {
        return bookingRepository.findByVenue(venueId);
    }

    @Cacheable(value = "bookings:venueId", key = "#venueId")
    public List<BookingDto> loadByVenue(long venueId) {
        List<Booking> bookings = bookingRepository.findByVenue(venueId);
        return mapper.bookingsToBookingDtoList(bookings);
    }

    public Optional<Booking> findById(long id) {
        return bookingRepository.findById(id);
    }

    @Cacheable(value = "booking:id", key = "#id")
    public BookingDto loadById(long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(NoSuchBookingException::new);
        return mapper.bookingToBookingDto(booking);
    }

    public List<Booking> findByUsername(String username) {
        return bookingRepository.findByUsername(username);
    }

    @Cacheable(value = "bookings:username", key = "#username")
    public List<BookingDto> loadByUsername(String username) {
        List<Booking> bookings = bookingRepository.findByUsername(username);
        return mapper.bookingsToBookingDtoList(bookings);
    }

    public Optional<Booking> findCompletedBookingById(long id) {
        return bookingRepository.findCompletedBookingById(id);
    }

    @Cacheable(value = "booking:complete", key = "#id")
    public BookingDto loadCompletedBookingById(long id) {
        Booking booking = bookingRepository.findCompletedBookingById(id).orElseThrow(NoSuchBookingException::new);
        return mapper.bookingToBookingDto(booking);
    }

    @Cacheable(value = "bookings:datetime", key = "#id")
    public List<BookingDateDto> bookingDatesByVenue(long id) {
        List<Booking> bookings = bookingRepository.findByVenue(id);
        return bookings.stream().map(mapper::bookingToBookingDateTimeDto).toList();
    }

    public Boolean isBookingAvailable(String bookingDate) {
        LocalDate localBookingDate = LocalDate.parse(bookingDate);
        LocalDate currentDate = LocalDate.now();

        if (bookingRepository.findByBookingDate(bookingDate).isPresent()) return false;
        if (!localBookingDate.isAfter(currentDate)) return false;

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


    @Transactional
    @CacheEvict(value = {"booking:id", "booking:complete", "bookings:venueId", "bookings:username", "bookings:datetime"},allEntries = true)
    public void deleteBooking(long id) {
        bookingRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = {"booking:id", "booking:complete", "bookings:venueId", "bookings:username", "bookings:datetime"},allEntries = true)
    public void updateStatus(long id, BookingStatus status) throws NoSuchBookingException {
        Booking booking = bookingRepository.findById(id).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    @Transactional
    @CacheEvict(value = {"booking:id", "booking:complete", "bookings:venueId", "bookings:username", "bookings:datetime"}, allEntries = true)
    public Booking addNewBooking(BookingDto body, Venue venue, String username) {
        Booking newBooking = Booking.builder()
                .bookingDate(body.bookingDate())
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(venue.getEstimate())
                .username(username)
                .phone(body.phone())
//                .email(body.email())
                .guests(body.guests()).build();
        bookingRepository.save(newBooking);
        return newBooking;
    }

    @Transactional
    @CacheEvict(value = {"booking:id", "booking:complete", "bookings:venueId", "bookings:username", "bookings:datetime"},allEntries = true)
    public void updateBooking(Booking booking, String newBookingDate) {
        booking.setBookingDate(newBookingDate);

        //setting a new reservation date
        String newReservation = LocalDateTime.now(ZoneId.of("PLT", ZoneId.SHORT_IDS)).plusMinutes(2).toString();
        booking.setReservationExpiry(newReservation);

        bookingRepository.save(booking);
    }

}

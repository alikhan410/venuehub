package com.venuehub.bookingservice.service;


import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.repository.BookingRepository;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.commons.exception.NoSuchVenueException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(RabbitAutoConfiguration.class)
@ActiveProfiles("test")
@Transactional
class BookingServiceTest {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private VenueService venueService;

    @Autowired
    private BookingRepository bookingRepository;

    long venueId;
    long bookingId;
    String username;
    String email;
    String phone;
    int estimates;
    int guests;
    String name;
    String bookingDateTime;
    BookingStatus status;
    Venue venue;

    @BeforeEach
    void runBefore() {

        venueId = 1L;
        bookingId = 1L;
        username = "test_user";
        email = "ali@gmail.com";
        phone = "03178923162";
        estimates = 45000;
        guests = 50;
        name = "Saffron Venue";
        bookingDateTime = "2024-12-04T18:30:00";
        status = BookingStatus.RESERVED;
        venue = venueService.findById(venueId).orElseThrow(NoSuchVenueException::new);

    }

    @Test
    void save() {
        Booking booking = Booking.builder()
                .bookingDateTime(bookingDateTime)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .email(email)
                .guests(guests).build();
        bookingService.save(booking);
        Booking newBooking = bookingRepository.findById(venueId).orElseThrow(NoSuchVenueException::new);

        assertThat(newBooking.getId()).isEqualTo(booking.getId());
        assertThat(newBooking.getBookingDateTime()).isEqualTo(bookingDateTime);
        assertThat(newBooking.getStatus()).isEqualTo(BookingStatus.RESERVED);
        assertThat(newBooking.getVenue().getId()).isEqualTo(venueId);
        assertThat(newBooking.getBookingFee()).isEqualTo(estimates);
        assertThat(newBooking.getUsername()).isEqualTo(username);
        assertThat(newBooking.getEmail()).isEqualTo(email);
        assertThat(newBooking.getPhone()).isEqualTo(phone);
        assertThat(newBooking.getGuests()).isEqualTo(guests);
    }

    @Test
    void findByVenue() {

        Booking booking = Booking.builder()
                .bookingDateTime(bookingDateTime)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .email(email)
                .guests(guests).build();
        bookingRepository.save(booking);
        List<Booking> bookings = bookingService.findByVenue(venueId);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
        assertThat(bookings.get(0).getVenue().getId()).isEqualTo(venueId);
        assertThat(bookings.get(0).getBookingDateTime()).isEqualTo(bookingDateTime);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.RESERVED);
        assertThat(bookings.get(0).getBookingFee()).isEqualTo(estimates);
        assertThat(bookings.get(0).getUsername()).isEqualTo(username);
        assertThat(bookings.get(0).getEmail()).isEqualTo(email);
        assertThat(bookings.get(0).getPhone()).isEqualTo(phone);
        assertThat(bookings.get(0).getGuests()).isEqualTo(guests);
    }

    @Test
    void isBookingAvailable() {

        Booking booking = Booking.builder()
                .bookingDateTime(bookingDateTime)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .email(email)
                .guests(guests).build();
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findByVenue(venueId);
        Boolean res = bookingService.isBookingAvailable(LocalDateTime.parse(bookingDateTime), bookings);
        Boolean res1 = bookingService.isBookingAvailable(LocalDateTime.parse("2024-12-05T18:30:00"), bookings);
        assertThat(res).isFalse();
        assertThat(res1).isTrue();
    }

    @Test
    void removeBooking() {
    }

    @Test
    void findById() {
    }

    @Test
    void findByUsername() {
    }

    @Test
    void findCompletedBookingById() {
    }

    @Test
    void deleteBooking() {
    }

    @Test
    void updateStatus() {
    }

    @Test
    void addNewBooking() {
    }

    @Test
    void updateBooking() {
    }
}
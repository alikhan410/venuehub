package com.venuehub.bookingservice.service;


import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.repository.BookingRepository;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.commons.exception.NoSuchVenueException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
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
    String bookingDate;
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
        bookingDate = "2024-12-04";
        status = BookingStatus.RESERVED;
        venue = venueService.findById(venueId).orElseThrow(NoSuchVenueException::new);

    }
    @AfterEach
    void afterEachSetUp(){
        bookingRepository.deleteAll();
    }

    @Test
    void save() {
        Booking booking = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests).build();
        bookingService.save(booking);
        Booking newBooking = bookingRepository.findById(booking.getId()).orElseThrow(NoSuchBookingException::new);

        assertThat(newBooking.getId()).isEqualTo(booking.getId());
        assertThat(newBooking.getBookingDate()).isEqualTo(bookingDate);
        assertThat(newBooking.getStatus()).isEqualTo(BookingStatus.RESERVED);
        assertThat(newBooking.getVenue().getId()).isEqualTo(venueId);
        assertThat(newBooking.getBookingFee()).isEqualTo(estimates);
        assertThat(newBooking.getUsername()).isEqualTo(username);
        assertThat(newBooking.getPhone()).isEqualTo(phone);
        assertThat(newBooking.getGuests()).isEqualTo(guests);
    }

    @Test
    void findByVenue() {

        Booking booking = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests).build();
        bookingRepository.save(booking);
        List<Booking> bookings = bookingService.findByVenue(venueId);
        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getId()).isEqualTo(booking.getId());
        assertThat(bookings.get(0).getVenue().getId()).isEqualTo(venueId);
        assertThat(bookings.get(0).getBookingDate()).isEqualTo(bookingDate);
        assertThat(bookings.get(0).getStatus()).isEqualTo(BookingStatus.RESERVED);
        assertThat(bookings.get(0).getBookingFee()).isEqualTo(estimates);
        assertThat(bookings.get(0).getUsername()).isEqualTo(username);
        assertThat(bookings.get(0).getPhone()).isEqualTo(phone);
        assertThat(bookings.get(0).getGuests()).isEqualTo(guests);
    }

    @Test
    void isBookingAvailable() {

        Booking booking = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests).build();
        bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findByVenue(venueId);

        Boolean res = bookingService.isBookingAvailable(bookingDate);
        String bookingDate2 = "2024-12-05";
        Boolean res1 = bookingService.isBookingAvailable(bookingDate2);
        assertThat(res).isFalse();
        assertThat(res1).isTrue();
    }

    @Test
    void removeBooking() {
        Booking booking1 = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests)
                .build();

        Booking booking2 = Booking.builder()
                .bookingDate("2024-12-05") // Different booking date
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests)
                .build();

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        List<Booking> updatedBookings = bookingService.removeBooking(bookings, booking1.getId());

        assertThat(updatedBookings.size()).isEqualTo(1);
        assertThat(updatedBookings.get(0).getId()).isEqualTo(booking2.getId());
    }

    @Test
    void findById() throws NoSuchBookingException {
        Booking booking = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests)
                .build();
        bookingRepository.save(booking);

        Optional<Booking> retrievedBooking = bookingService.findById(booking.getId());

        assertThat(retrievedBooking).isPresent();
        assertThat(retrievedBooking.get().getId()).isEqualTo(booking.getId());
        assertThat(retrievedBooking.get().getBookingDate()).isEqualTo(bookingDate);
    }

    @Test
    void findByUsername() {
        Booking booking = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests)
                .build();
        bookingRepository.save(booking);

        List<Booking> bookings = bookingService.findByUsername(username);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.get(0).getUsername()).isEqualTo(username);
    }

    @Test
    void findCompletedBookingById() {
        Booking booking = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.COMPLETED) // Set status to COMPLETED
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests)
                .build();
        bookingRepository.save(booking);

        Optional<Booking> completedBooking = bookingService.findCompletedBookingById(booking.getId());

        assertThat(completedBooking).isPresent();
        assertThat(completedBooking.get().getStatus()).isEqualTo(BookingStatus.COMPLETED);
    }

    @Test
    void updateStatus() throws NoSuchBookingException {
        Booking booking = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests)
                .build();
        bookingRepository.save(booking);

        bookingService.updateStatus(booking.getId(), BookingStatus.COMPLETED);

        Optional<Booking> updatedBooking = bookingRepository.findById(booking.getId());

        assertThat(updatedBooking.isPresent()).isTrue();
        assertThat(updatedBooking.get().getStatus()).isEqualTo(BookingStatus.COMPLETED);
    }

    @Test
    void updateBooking() {
        Booking booking = Booking.builder()
                .bookingDate(bookingDate)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .guests(guests)
                .build();
        bookingRepository.save(booking);

        String newBookingDate = "2024-12-07";
        bookingService.updateBooking(booking, newBookingDate);

        Optional<Booking> updatedBooking = bookingRepository.findById(booking.getId());

        assertThat(updatedBooking.isPresent()).isTrue();
        assertThat(updatedBooking.get().getBookingDate()).isEqualTo(newBookingDate);
    }
}
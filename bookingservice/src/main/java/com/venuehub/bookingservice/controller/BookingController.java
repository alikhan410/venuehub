package com.venuehub.bookingservice.controller;

import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.bookingservice.response.*;
import com.venuehub.bookingservice.utils.SecurityChecks;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.event.job.BookingJobCancellingEvent;
import com.venuehub.broker.event.job.BookingJobSchedulingEvent;
import com.venuehub.broker.producer.booking.BookingCreatedProducer;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import com.venuehub.broker.producer.job.BookingJobCancellingProducer;
import com.venuehub.broker.producer.job.BookingJobSchedulingProducer;
import com.venuehub.commons.exception.*;
import com.venuehub.bookingservice.mapper.Mapper;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.service.BookingService;
import com.venuehub.bookingservice.service.VenueService;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.List;

@RestController
@Validated
public class BookingController {
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final VenueService venueService;
    private final BookingCreatedProducer bookingCreatedProducer;
    private final BookingUpdatedProducer bookingUpdatedProducer;
    private final BookingJobSchedulingProducer bookingJobSchedulingProducer;
    private final BookingJobCancellingProducer bookingJobCancellingProducer;

    @Autowired
    public BookingController(BookingService bookingService, VenueService venueService, BookingCreatedProducer bookingCreatedProducer, BookingUpdatedProducer bookingUpdatedProducer, BookingJobSchedulingProducer bookingJobSchedulingProducer, BookingJobCancellingProducer bookingJobCancellingProducer) {
        this.venueService = venueService;
        this.bookingService = bookingService;
        this.bookingCreatedProducer = bookingCreatedProducer;
        this.bookingUpdatedProducer = bookingUpdatedProducer;
        this.bookingJobSchedulingProducer = bookingJobSchedulingProducer;
        this.bookingJobCancellingProducer = bookingJobCancellingProducer;
    }

    @PostMapping("/bookings/venue/{venueId}")
    @Transactional
    public ResponseEntity<BookingDto> addBooking(@PathVariable long venueId, @Valid @RequestBody BookingDto body, @AuthenticationPrincipal Jwt jwt) throws BookingUnavailableException {
        logger.info("Received request to add a booking for venueId: {} by user: {}", venueId, jwt.getSubject());

        if (!jwt.getClaim("loggedInAs").equals("USER")) {
            logger.warn("User: {} is not logged in as USER", jwt.getSubject());
            throw new UserForbiddenException("Please login as user and try again");
        }
        SecurityChecks.userCheck(jwt);

        Venue venue = venueService.findById(venueId).orElseThrow(() -> {
            logger.error("Venue not found with id: {}", venueId);
            return new NoSuchVenueException();
        });
        List<Booking> bookings = venue.getBookings();
        LocalDateTime bookingDateTime = LocalDateTime.parse(body.bookingDateTime());

        //LocalDateTime today = LocalDateTime.parse("2019-03-27T10:15:30")
        if (!bookingService.isBookingAvailable(bookingDateTime, bookings)) {
            logger.warn("Booking is not available for date: {}", bookingDateTime);
            throw new BookingUnavailableException("Booking is not available for date: "+bookingDateTime);
        }

        Booking newBooking = bookingService.addNewBooking(body, venue, jwt.getSubject());
        logger.info("Booking added with id: {} for venueId: {}", newBooking.getId(), venueId);

        //Sending the event
        BookingCreatedEvent bookingCreatedEvent = new BookingCreatedEvent(
                newBooking.getId(),
                venueId,
                BookingStatus.RESERVED,
                newBooking.getBookingFee(),
                jwt.getSubject()
        );
        BookingJobSchedulingEvent bookingJobSchedulingEvent = new BookingJobSchedulingEvent(
                newBooking.getId(),
                BookingStatus.RESERVED,
                newBooking.getBookingDateTime(),
                newBooking.getReservationExpiry(),
                jwt.getSubject()

        );

        bookingCreatedProducer.produce(bookingCreatedEvent, MyExchange.VENUE_EXCHANGE);
        bookingCreatedProducer.produce(bookingCreatedEvent, MyExchange.PAYMENT_EXCHANGE);

        bookingJobSchedulingProducer.produce(bookingJobSchedulingEvent, MyExchange.JOB_EXCHANGE);

        BookingDto bookingDto = Mapper.modeltoBookingDto(newBooking);
        return new ResponseEntity<>(bookingDto, HttpStatus.CREATED);
    }

    @GetMapping("/bookings/venue/{venueId}")
    public ResponseEntity<BookingDateListResponse> getBookingByVenue(@PathVariable Long venueId) {
        logger.info("Received request to get bookings for venueId: {}", venueId);

        venueService.findById(venueId).orElseThrow(() -> {
            logger.error("Venue not found with id: {}", venueId);
            return new NoSuchVenueException();
        });

        List<Booking> bookingList = bookingService.findByVenue(venueId);
        List<BookingDateDto> bookedVenueDtoList = bookingList.stream().map(Mapper::modelToBookingDateDto).toList();

        BookingDateListResponse response = new BookingDateListResponse(bookedVenueDtoList);

        logger.info("Returning bookings for venueId: {}", venueId);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingStatus(@PathVariable long bookingId, @AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException {
        logger.info("Received request to get booking status for bookingId: {} by user: {}", bookingId, jwt.getSubject());

        SecurityChecks.userCheck(jwt);

        Booking booking = bookingService.findById(bookingId).orElseThrow(() -> {
            logger.error("Booking not found with id: {}", bookingId);
            return new NoSuchBookingException();
        });

        if (!booking.getUsername().equals(jwt.getSubject())) {
            logger.warn("User: {} is not authorized to access booking id: {}", jwt.getSubject(), bookingId);
            throw new UserForbiddenException();
        }

        BookingResponse response = new BookingResponse(booking.getBookingDateTime(), booking.getGuests(), booking.getStatus(), booking.getVenue().getName(), booking.getVenue().getId());

        logger.info("Returning booking status for bookingId: {}", bookingId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/bookings/user")
    public ResponseEntity<List<GetBookingsResponse>> getBookingByUser(@AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException {
        logger.info("Received request to get bookings by user: {}", jwt.getSubject());

        SecurityChecks.userCheck(jwt);

        List<Booking> bookingList = bookingService.findByUsername(jwt.getSubject());
        List<GetBookingsResponse> bookingsList = bookingList.stream().map(Mapper::modelToResponse).toList();

        logger.info("Returning bookings for user: {}", jwt.getSubject());
        return new ResponseEntity<>(bookingsList, HttpStatus.OK);
    }

    @GetMapping("/bookings/vendor")
    public ResponseEntity<List<GetBookingsResponse>> getBookingByVendor(@AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException {
        logger.info("Received request to get bookings by vendor: {}", jwt.getSubject());
        SecurityChecks.vendorCheck(jwt, "Please login as vendor and try again");

        List<Booking> bookings = venueService.findByUsername(jwt.getSubject())
                .stream()
                .flatMap(venue -> venue.getBookings().stream())
                .toList();

        List<GetBookingsResponse> bookingsList = bookings.stream().map(Mapper::modelToResponse).toList();

        logger.info("Returning bookings for vendor: {}", jwt.getSubject());
        return new ResponseEntity<>(bookingsList, HttpStatus.OK);
    }

    @DeleteMapping("/bookings/{bookingId}")
    @Transactional
    public ResponseEntity<HttpStatus> cancelBooking(@PathVariable long bookingId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        logger.info("Received request to cancel booking with id: {} by user: {}", bookingId, jwt.getSubject());
        SecurityChecks.userCheck(jwt);

        Booking booking = bookingService.findById(bookingId).orElseThrow(() -> {
            logger.error("Booking not found with id: {}", bookingId);
            return new NoSuchBookingException();
        });

        if (!booking.getUsername().equals(jwt.getSubject())) {
            logger.warn("User: {} is not authorized to cancel booking id: {}", jwt.getSubject(), bookingId);
            throw new UserForbiddenException();
        }

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            logger.error("Booking id: {} is not in BOOKED status, cannot cancel", bookingId);
            //TODO Add a new exception
            throw new Exception("Not booked");
        }

        //updating the booking as failed
        booking.setStatus(BookingStatus.FAILED);
        bookingService.save(booking);

        //sending the events
        BookingUpdatedEvent bookingUpdatedEvent = new BookingUpdatedEvent(bookingId, BookingStatus.FAILED);
        BookingJobCancellingEvent bookingJobCancellingEvent = new BookingJobCancellingEvent(bookingId, BookingStatus.FAILED);

        bookingUpdatedProducer.produce(bookingUpdatedEvent, MyExchange.VENUE_EXCHANGE);
        bookingUpdatedProducer.produce(bookingUpdatedEvent, MyExchange.PAYMENT_EXCHANGE);

        bookingJobCancellingProducer.produce(bookingJobCancellingEvent, MyExchange.JOB_EXCHANGE);

        logger.info("Booking with id: {} cancelled by user: {}", bookingId, jwt.getSubject());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/bookings/{bookingId}")
    @Transactional
    public ResponseEntity<HttpStatus> updateBookingDate(@PathVariable long bookingId, @RequestBody BookingDateDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {
        logger.info("Received request to update booking date for bookingId: {} by user: {}", bookingId, jwt.getSubject());

        SecurityChecks.userCheck(jwt);

        Booking booking = bookingService.findById(bookingId).orElseThrow(() -> {
            logger.error("Booking not found with id: {}", bookingId);
            return new NoSuchBookingException();
        });

        if (!booking.getUsername().equals(jwt.getSubject())) {
            logger.warn("User: {} is not authorized to update booking id: {}", jwt.getSubject(), bookingId);
            throw new UserForbiddenException();
        }

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            logger.error("Booking id: {} is not in BOOKED status, cannot update date", bookingId);
            //TODO Add a new exception
            throw new Exception("Not booked");
        }

        //updating both the booking and reservation
        bookingService.updateBooking(booking, body.BookingDate());

        //Sending the events
        BookingUpdatedEvent bookingUpdatedEvent = new BookingUpdatedEvent(bookingId, BookingStatus.RESERVED);
        BookingJobSchedulingEvent bookingJobSchedulingEvent = new BookingJobSchedulingEvent(
                bookingId,
                BookingStatus.RESERVED,
                booking.getBookingDateTime(),
                booking.getReservationExpiry(),
                jwt.getSubject()

        );
        bookingUpdatedProducer.produce(bookingUpdatedEvent, MyExchange.VENUE_EXCHANGE);
        bookingUpdatedProducer.produce(bookingUpdatedEvent, MyExchange.PAYMENT_EXCHANGE);
        bookingJobSchedulingProducer.produce(bookingJobSchedulingEvent, MyExchange.JOB_EXCHANGE);

        logger.info("Booking date for bookingId: {} updated by user: {}", bookingId, jwt.getSubject());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

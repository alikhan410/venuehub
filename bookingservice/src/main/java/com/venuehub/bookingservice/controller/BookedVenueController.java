package com.venuehub.bookingservice.controller;

import com.venuehub.bookingservice.dto.BookedVenueDto;
import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.bookingservice.response.GetBookingByUsernameResponse;
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
import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.response.BookedVenueListResponse;
import com.venuehub.bookingservice.service.BookedVenueService;
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
public class BookedVenueController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookedVenueController.class);
    private final BookedVenueService bookedVenueService;
    private final VenueService venueService;
    private final BookingCreatedProducer bookingCreatedProducer;
    private final BookingUpdatedProducer bookingUpdatedProducer;
    private final BookingJobSchedulingProducer bookingJobSchedulingProducer;
    private final BookingJobCancellingProducer bookingJobCancellingProducer;

    @Autowired
    public BookedVenueController(BookedVenueService bookedVenueService, VenueService venueService, BookingCreatedProducer bookingCreatedProducer, BookingUpdatedProducer bookingUpdatedProducer, BookingJobSchedulingProducer bookingJobSchedulingProducer, BookingJobCancellingProducer bookingJobCancellingProducer) {
        this.venueService = venueService;
        this.bookedVenueService = bookedVenueService;
        this.bookingCreatedProducer = bookingCreatedProducer;
        this.bookingUpdatedProducer = bookingUpdatedProducer;
        this.bookingJobSchedulingProducer = bookingJobSchedulingProducer;
        this.bookingJobCancellingProducer = bookingJobCancellingProducer;
    }

    @PostMapping("/booking/{venueId}")
    @Transactional
    public ResponseEntity<BookedVenueDto> addBooking(@PathVariable long venueId, @Valid @RequestBody BookedVenueDto body, @AuthenticationPrincipal Jwt jwt) throws BookingUnavailableException {

        if(jwt.getClaim("loggedInAs")!="USER"){
            throw new UserForbiddenException();
        }

        Venue venue = venueService.findById(venueId).orElseThrow(NoSuchVenueException::new);
        List<BookedVenue> bookings = venue.getBookings();
        LocalDateTime bookingDateTime = LocalDateTime.parse(body.bookingDateTime());

//        LocalDateTime today = LocalDateTime.parse("2019-03-27T10:15:30")
        if (!bookedVenueService.isBookingAvailable(bookingDateTime, bookings)) {
            throw new BookingUnavailableException();
        }

        BookedVenue newBooking = bookedVenueService.addNewBooking(body, venue, jwt.getSubject());

        //Sending the event
        BookingCreatedEvent bookingCreatedEvent = new BookingCreatedEvent(
                newBooking.getId(),
                venueId,
                BookingStatus.RESERVED,
                jwt.getSubject()
        );
        BookingJobSchedulingEvent bookingJobSchedulingEvent = new BookingJobSchedulingEvent(
                newBooking.getId(),
                BookingStatus.RESERVED,
                newBooking.getBookingDateTime(),
                jwt.getSubject()

        );

        bookingCreatedProducer.produce(bookingCreatedEvent, MyExchange.VENUE_EXCHANGE);
        bookingCreatedProducer.produce(bookingCreatedEvent, MyExchange.PAYMENT_EXCHANGE);

        bookingJobSchedulingProducer.produce(bookingJobSchedulingEvent,MyExchange.JOB_EXCHANGE);

        BookedVenueDto bookedVenueDto = Mapper.modelToDto(newBooking);
        return new ResponseEntity<>(bookedVenueDto, HttpStatus.CREATED);
    }

    @GetMapping("/booking/venue/{venueId}")
    public ResponseEntity<BookedVenueListResponse> getBookingByVenue(@PathVariable Long venueId) {

        venueService.findById(venueId).orElseThrow(NoSuchVenueException::new);

        List<BookedVenue> bookedVenueList = bookedVenueService.findByVenue(venueId);

        List<BookedVenueDto> bookedVenueDtoList = bookedVenueList.stream().map(Mapper::modelToDto).toList();

        BookedVenueListResponse response = new BookedVenueListResponse(bookedVenueDtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/booking/{bookingId}")
    public BookedVenue getBookingById(@PathVariable long bookingId) throws NoSuchBookingException {
        return bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);
    }

    @GetMapping("/booking")
    public ResponseEntity<List<GetBookingByUsernameResponse>> getBookingByUsername(@AuthenticationPrincipal Jwt jwt) throws NoSuchBookingException {

        if(jwt.getClaim("loggedInAs")!="USER"){
            throw new UserForbiddenException();
        }

        List<BookedVenue> bookedVenueList = bookedVenueService.findByUsername(jwt.getSubject());

        List<GetBookingByUsernameResponse> bookedVenueDtoList = bookedVenueList.stream().map(Mapper::modelToResponse).toList();

        return new ResponseEntity<>(bookedVenueDtoList, HttpStatus.OK);
    }

    @DeleteMapping("/booking/{bookingId}")
    @Transactional
    public ResponseEntity<HttpStatus> cancelBooking(@PathVariable long bookingId, @AuthenticationPrincipal Jwt jwt) throws Exception {

        if(jwt.getClaim("loggedInAs")!="USER"){
            throw new UserForbiddenException();
        }

        BookedVenue booking = bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);

        if (!booking.getUsername().equals(jwt.getSubject())) {
            throw new UserForbiddenException();
        }

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            //TODO add a BookingCancellationException
            throw new Exception("Not booked");
        }

        //updating the booking as failed
        booking.setStatus(BookingStatus.FAILED);
        bookedVenueService.save(booking);

        //sending the events
        BookingUpdatedEvent bookingUpdatedEvent = new BookingUpdatedEvent(bookingId, BookingStatus.FAILED);
        BookingJobCancellingEvent bookingJobCancellingEvent = new BookingJobCancellingEvent(bookingId, BookingStatus.FAILED);

        bookingUpdatedProducer.produce(bookingUpdatedEvent, MyExchange.VENUE_EXCHANGE);
        bookingUpdatedProducer.produce(bookingUpdatedEvent, MyExchange.PAYMENT_EXCHANGE);

        bookingJobCancellingProducer.produce(bookingJobCancellingEvent, MyExchange.JOB_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/booking/{bookingId}")
    @Transactional
    public ResponseEntity<HttpStatus> updateBookingDate(@PathVariable long bookingId, @RequestBody BookingDateDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {

        if(jwt.getClaim("loggedInAs")!="USER"){
            throw new UserForbiddenException();
        }

        BookedVenue booking = bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);

        if (!booking.getUsername().equals(jwt.getSubject())) {
            throw new UserForbiddenException();
        }

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            //TODO add a BookingCancellationException
            throw new Exception("Not booked");
        }

        booking.setBookingDateTime(body.BookingDate());
        bookedVenueService.save(booking);

        //Sending the event
        BookingUpdatedEvent bookingUpdatedEvent = new BookingUpdatedEvent(bookingId, BookingStatus.RESERVED);
        BookingJobSchedulingEvent bookingJobSchedulingEvent = new BookingJobSchedulingEvent(
                bookingId,
                BookingStatus.RESERVED,
                body.BookingDate(),
                jwt.getSubject()

        );
        bookingUpdatedProducer.produce(bookingUpdatedEvent, MyExchange.VENUE_EXCHANGE);
        bookingUpdatedProducer.produce(bookingUpdatedEvent, MyExchange.PAYMENT_EXCHANGE);
        bookingJobSchedulingProducer.produce(bookingJobSchedulingEvent,MyExchange.JOB_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

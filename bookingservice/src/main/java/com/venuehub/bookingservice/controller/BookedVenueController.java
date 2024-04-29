package com.venuehub.bookingservice.controller;

import com.venuehub.bookingservice.dto.BookedVenueDto;
import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.booking.BookingCreatedProducer;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import com.venuehub.commons.exception.*;
import com.venuehub.bookingservice.mapper.Mapper;
import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.response.BookedVenueListResponse;
import com.venuehub.bookingservice.service.BookedVenueService;
import com.venuehub.bookingservice.service.JobService;
import com.venuehub.bookingservice.service.VenueService;

import jakarta.validation.Valid;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@Validated
public class BookedVenueController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookedVenueController.class);
    private final BookedVenueService bookedVenueService;
    private final VenueService venueService;
    private final JobService jobService;
    private final BookingCreatedProducer bookingCreatedProducer;
    private final BookingUpdatedProducer bookingUpdatedProducer;

    @Autowired
    public BookedVenueController(JobService jobService, BookedVenueService bookedVenueService, VenueService venueService, BookingCreatedProducer bookingCreatedProducer, BookingUpdatedProducer bookingUpdatedProducer) {
        this.venueService = venueService;
        this.bookedVenueService = bookedVenueService;
        this.jobService = jobService;
        this.bookingCreatedProducer = bookingCreatedProducer;
        this.bookingUpdatedProducer = bookingUpdatedProducer;
    }

    @PostMapping("/booking/{venueId}")
    public ResponseEntity<BookedVenueDto> addBooking(
            @PathVariable long venueId,
            @Valid @RequestBody BookedVenueDto body,
            @AuthenticationPrincipal Jwt jwt
    ) throws BookingUnavailableException, SchedulerException {

        Venue venue = venueService.findById(venueId).orElseThrow(NoSuchVenueException::new);
        List<BookedVenue> bookings = venue.getBookings();
        LocalDateTime bookingDateTime = LocalDateTime.parse(body.bookingDateTime());

//        LocalDateTime today = LocalDateTime.parse("2019-03-27T10:15:30")
        if (!bookedVenueService.isBookingAvailable(bookingDateTime, bookings)) {
            throw new BookingUnavailableException();
        }

        BookedVenue newBooking = bookedVenueService.addNewBooking(body, venue, jwt.getSubject());

        //Starting  a booking removing job
        JobDetail bookingjobDetail = jobService.buildBookingJob(newBooking.getId());
        Trigger bookingJobTrigger = jobService.buildBookingJobTrigger(bookingjobDetail, bookingDateTime);
        jobService.scheduleJob(bookingjobDetail, bookingJobTrigger);

        //starting a reservation job
        JobDetail reservationJobDetail = jobService.buildReservationJob(newBooking.getId());
        Trigger reservationJobTrigger = jobService.buildReservationJob(reservationJobDetail);
        jobService.scheduleJob(reservationJobDetail, reservationJobTrigger);

        //Sending the event
        BookingCreatedEvent event = new BookingCreatedEvent(
                newBooking.getId(),
                venueId,
                BookingStatus.RESERVED,
                jwt.getSubject()
        );

        bookingCreatedProducer.produce(event, MyExchange.VENUE_EXCHANGE);
        bookingCreatedProducer.produce(event, MyExchange.PAYMENT_EXCHANGE);

        BookedVenueDto bookedVenueDto = Mapper.modelToDto(newBooking);
        return new ResponseEntity<>(bookedVenueDto, HttpStatus.CREATED);
    }

    @PostMapping("/reserve/{id}")
    public String addReservation(@PathVariable long id) {
        return null;
    }


    @GetMapping("/booking")
    public ResponseEntity<BookedVenueListResponse> getBookingByVenue(@RequestParam("venue") long id) {

        venueService.findById(id).orElseThrow(NoSuchVenueException::new);

        List<BookedVenue> bookedVenueList = bookedVenueService.findByVenue(id);

        List<BookedVenueDto> bookedVenueDtoList = bookedVenueList.stream().map(Mapper::modelToDto).toList();

        BookedVenueListResponse response = new BookedVenueListResponse(bookedVenueDtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @GetMapping("/booking/{bookingId}")
    public BookedVenue getBookingById(@PathVariable long bookingId) throws NoSuchBookingException {
        return bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);
    }

    @DeleteMapping("/booking/{bookingId}")
    public ResponseEntity<HttpStatus> deleteBooking(@PathVariable long bookingId, @AuthenticationPrincipal Jwt jwt) throws Exception {
        BookedVenue booking = bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);

        if (!booking.getUsername().equals(jwt.getSubject())) {
            throw new UserForbiddenException();
        }

        if (booking.getStatus().equals(BookingStatus.BOOKED)) {
            //TODO add a BookingCancellationException
        }

        //cancelling the booking job
        jobService.cancelBookingJob(String.valueOf(bookingId));

        //updating the booking as failed
        booking.setStatus(BookingStatus.FAILED);
        bookedVenueService.save(booking);

        //sending the booking updated event
        BookingUpdatedEvent event = new BookingUpdatedEvent(bookingId, BookingStatus.FAILED);
        bookingUpdatedProducer.produce(event, MyExchange.VENUE_EXCHANGE);
        bookingUpdatedProducer.produce(event, MyExchange.PAYMENT_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/booking/{bookingId}")
    public ResponseEntity<HttpStatus> updateBookingDate(@PathVariable long bookingId, @RequestBody BookingDateDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {
        BookedVenue booking = bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);

        if (!booking.getUsername().equals(jwt.getSubject())) {
            throw new UserForbiddenException();
        }

        if (booking.getStatus().equals(BookingStatus.BOOKED)) {
            //TODO add a BookingCancellationException
        }

        booking.setBookingDateTime(body.BookingDate());
        bookedVenueService.save(booking);

        //cancelling previous scheduled jobs
        jobService.cancelBookingJob(String.valueOf(bookingId));
        jobService.cancelReservationJob(String.valueOf(bookingId));

        LocalDateTime bookingDateTime = LocalDateTime.parse(body.BookingDate());

        //Starting  a booking job
        JobDetail bookingjobDetail = jobService.buildBookingJob(bookingId);
        Trigger bookingJobTrigger = jobService.buildBookingJobTrigger(bookingjobDetail, bookingDateTime);
        jobService.scheduleJob(bookingjobDetail, bookingJobTrigger);

        //starting a reservation job
        JobDetail reservationJobDetail = jobService.buildReservationJob(bookingId);
        Trigger reservationJobTrigger = jobService.buildReservationJob(reservationJobDetail);
        jobService.scheduleJob(reservationJobDetail, reservationJobTrigger);

        //Sending the event
        BookingUpdatedEvent event = new BookingUpdatedEvent(bookingId, BookingStatus.RESERVED);
        bookingUpdatedProducer.produce(event, MyExchange.VENUE_EXCHANGE);
        bookingUpdatedProducer.produce(event, MyExchange.PAYMENT_EXCHANGE);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

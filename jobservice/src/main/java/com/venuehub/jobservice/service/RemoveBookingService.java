package com.venuehub.jobservice.service;

import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.jobservice.entity.Booking;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

@Service
public class RemoveBookingService extends QuartzJobBean {
    private final Logger LOGGER = LoggerFactory.getLogger(RemoveBookingService.class);
    private final BookingService bookedVenueService;
    private final BookingUpdatedProducer producer;
    @Autowired
    public RemoveBookingService(BookingService bookedVenueService, BookingUpdatedProducer producer) {
        this.bookedVenueService = bookedVenueService;
        this.producer = producer;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        JobDataMap jobDataMap = context.getMergedJobDataMap();

        long bookingId = (long) jobDataMap.get("bookingId");

        try {
            removeBooking(bookingId);
        } catch (Exception e) {
            throw new JobExecutionException();
        }
    }

    public void removeBooking(long bookingId) {
        Booking booking = bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);

        if (booking.getStatus() == BookingStatus.FAILED) {
            LOGGER.info("Booking Failed");
            return;
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookedVenueService.save(booking);

        LOGGER.info("Booking Completed");

        BookingUpdatedEvent event = new BookingUpdatedEvent(
                bookingId,
                BookingStatus.COMPLETED
        );

        producer.produce(event, MyExchange.VENUE_EXCHANGE);
        producer.produce(event, MyExchange.BOOKING_EXCHANGE);
        producer.produce(event, MyExchange.PAYMENT_EXCHANGE);
    }
}
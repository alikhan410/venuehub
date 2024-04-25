package com.venuehub.bookingservice.service;

import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.BookingUpdatedProducer;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.broker.constants.BookingStatus;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class RemoveReservationService extends QuartzJobBean {
    @Autowired
    BookedVenueService bookedVenueService;
    @Autowired
    BookingUpdatedProducer producer;

    private final Logger LOGGER = LoggerFactory.getLogger(RemoveReservationService.class);

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

    public void removeBooking(long bookingId) throws Exception {
        BookedVenue booking = bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);
        if (booking.getStatus() == BookingStatus.BOOKED) {
            return;
        }
        bookedVenueService.updateStatus(bookingId, BookingStatus.FAILED);
        LOGGER.info("Booking reservation removed, customer unable to pay");
        BookingUpdatedEvent event = new BookingUpdatedEvent(
                bookingId,
                BookingStatus.FAILED
        );
        producer.produce(event);
    }
}

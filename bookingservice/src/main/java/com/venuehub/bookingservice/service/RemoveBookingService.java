package com.venuehub.bookingservice.service;

import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.BookingUpdatedProducer;
import com.venuehub.commons.exception.NoSuchBookingException;
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
    @Autowired
    BookedVenueService bookedVenueService;

    @Autowired
    BookingUpdatedProducer producer;

    private final Logger LOGGER = LoggerFactory.getLogger(RemoveBookingService.class);

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
        bookedVenueService.updateStatus(bookingId, BookingStatus.COMPLETED);
        BookedVenue booking = bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);
        LOGGER.info("Booking Completed");
        BookingUpdatedEvent event = new BookingUpdatedEvent(
                bookingId,
                BookingStatus.COMPLETED
                );
        producer.produce(event);
    }
}

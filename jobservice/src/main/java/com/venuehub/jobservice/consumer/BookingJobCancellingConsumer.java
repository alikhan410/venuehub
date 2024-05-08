package com.venuehub.jobservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.job.BookingJobCancellingEvent;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.jobservice.entity.BookedVenue;
import com.venuehub.jobservice.service.BookedVenueService;
import com.venuehub.jobservice.service.JobService;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingJobCancellingConsumer extends BaseConsumer<BookingJobCancellingEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingJobCancellingConsumer.class);
    private final BookedVenueService bookedVenueService;
    private final JobService jobService;

    @Autowired
    public BookingJobCancellingConsumer(BookedVenueService bookedVenueService, JobService jobService) {
        this.bookedVenueService = bookedVenueService;
        this.jobService = jobService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.JOB_CANCELLING_QUEUE_JOB_SERVICE)
    @Transactional
    public void consume(BookingJobCancellingEvent event) {
        LOGGER.info(event.getClass().getSimpleName() + " reached " + getClass().getSimpleName() + event);

        BookedVenue booking = bookedVenueService.findById(event.bookingId()).orElseThrow(NoSuchBookingException::new);
        booking.setStatus(event.status());
        bookedVenueService.save(booking);

        try {
            jobService.cancelBookingJob(String.valueOf(event.bookingId()));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

    }
}
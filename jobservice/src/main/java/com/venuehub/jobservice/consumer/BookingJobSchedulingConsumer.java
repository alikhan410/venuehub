package com.venuehub.jobservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.job.BookingJobSchedulingEvent;
import com.venuehub.jobservice.entity.BookedVenue;
import com.venuehub.jobservice.service.BookedVenueService;
import com.venuehub.jobservice.service.JobService;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingJobSchedulingConsumer extends BaseConsumer<BookingJobSchedulingEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingJobSchedulingConsumer.class);
    private final BookedVenueService bookedVenueService;
    private final JobService jobService;

    @Autowired
    public BookingJobSchedulingConsumer(BookedVenueService bookedVenueService, JobService jobService) {
        this.bookedVenueService = bookedVenueService;
        this.jobService = jobService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.JOB_SCHEDULING_QUEUE_JOB_SERVICE)
    public void consume(BookingJobSchedulingEvent event) {
        LOGGER.info(event.getClass().getSimpleName() + " reached " + getClass().getSimpleName() + event);

        //Saving the booking in db
        BookedVenue booking = new BookedVenue();
        booking.setId(event.bookingId());
        booking.setStatus(event.status());
        booking.setUsername(event.username());
        bookedVenueService.save(booking);

        LocalDateTime bookingDateTime = LocalDateTime.parse(event.bookingDateTime());

        //cancelling the booking job for this id if it has any
        try {
            jobService.cancelBookingJob(String.valueOf(booking.getId()));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        //cancelling the reservation job for this id if it has any
        try {
            jobService.cancelReservationJob(String.valueOf(booking.getId()));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        try {
            //Starting  a booking removing job
            JobDetail bookingjobDetail = jobService.buildBookingJob(booking.getId());
            Trigger bookingJobTrigger = jobService.buildBookingJobTrigger(bookingjobDetail, bookingDateTime);
            jobService.scheduleJob(bookingjobDetail, bookingJobTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        try {
            //starting a reservation job
            JobDetail reservationJobDetail = jobService.buildReservationJob(booking.getId());
            Trigger reservationJobTrigger = jobService.buildReservationJobTrigger(reservationJobDetail);
            jobService.scheduleJob(reservationJobDetail, reservationJobTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.venuehub.jobservice.consumer;

import com.venuehub.broker.constants.MyQueue;
import com.venuehub.broker.consumer.BaseConsumer;
import com.venuehub.broker.event.job.BookingJobSchedulingEvent;
import com.venuehub.jobservice.entity.Booking;
import com.venuehub.jobservice.service.BookingService;
import com.venuehub.jobservice.service.JobService;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingJobSchedulingConsumer extends BaseConsumer<BookingJobSchedulingEvent> {
    private final Logger LOGGER = LoggerFactory.getLogger(BookingJobSchedulingConsumer.class);
    private final BookingService bookedVenueService;
    private final JobService jobService;

    @Autowired
    public BookingJobSchedulingConsumer(BookingService bookedVenueService, JobService jobService) {
        this.bookedVenueService = bookedVenueService;
        this.jobService = jobService;
    }

    @Override
    @RabbitListener(queues = MyQueue.Constants.JOB_SCHEDULING_QUEUE_JOB_SERVICE)
    @Transactional
    public void consume(BookingJobSchedulingEvent event) {
        LOGGER.info("{} reached {} {}", event.getClass().getSimpleName(), getClass().getSimpleName(), event);

        //Saving the booking in db
        Booking booking = new Booking();
        booking.setId(event.bookingId());
        booking.setStatus(event.status());
        booking.setUsername(event.username());
        bookedVenueService.save(booking);

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
            Trigger bookingJobTrigger = jobService.buildBookingJobTrigger(bookingjobDetail, event.bookingDate());
            jobService.scheduleJob(bookingjobDetail, bookingJobTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }

        try {
            //starting a reservation job
            JobDetail reservationJobDetail = jobService.buildReservationJob(booking.getId());
            Trigger reservationJobTrigger = jobService.buildReservationJobTrigger(reservationJobDetail, event.reservationExpiry());
            jobService.scheduleJob(reservationJobDetail, reservationJobTrigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.venuehub.jobservice.consumer;

import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.event.job.BookingJobSchedulingEvent;
import com.venuehub.jobservice.service.BookingService;
import com.venuehub.jobservice.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;


class BookingJobSchedulingConsumerTest {

    @Mock
    private BookingService bookedVenueService;

    @Mock
    private JobService jobService;

    @InjectMocks
    private BookingJobSchedulingConsumer bookingJobSchedulingConsumer;

    private Long bookingId;
    private BookingStatus status;
    private String username;
    private String bookingDateTime;
    private String reservationExpiry;

    @BeforeEach
    public void setUp() {
        bookingId = 1L;
        status = BookingStatus.RESERVED;
        username = "test_user";
        bookingDateTime = "2024-05-04T10:15:30";
        reservationExpiry = "2024-05-04T10:17:30";

        //Initializes the mock objects annotated with @Mock, @Spy, and @InjectMocks.
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConsume() throws SchedulerException {
        BookingJobSchedulingEvent event = new BookingJobSchedulingEvent(bookingId, status, bookingDateTime,reservationExpiry, username);

        // Mocking jobDetail and trigger
        JobDetail bookingJobDetail = mock(JobDetail.class);
        Trigger bookingJobTrigger = mock(Trigger.class);
        JobDetail reservationJobDetail = mock(JobDetail.class);
        Trigger reservationJobTrigger = mock(Trigger.class);

        // Mocking jobService methods
        Mockito.when(jobService.buildBookingJob(1L)).thenReturn(bookingJobDetail);
        Mockito.when(jobService.buildBookingJobTrigger(any(JobDetail.class), any())).thenReturn(bookingJobTrigger);
        Mockito.when(jobService.buildReservationJob(1L)).thenReturn(reservationJobDetail);
        Mockito.when(jobService.buildReservationJobTrigger(any(JobDetail.class), any(String.class))).thenReturn(reservationJobTrigger);

        bookingJobSchedulingConsumer.consume(event);

        // Verify that the bookedVenueService.save() method is called once
        Mockito.verify(bookedVenueService, times(1)).save(any());

        // Verify that the jobService.cancelBookingJob() method is called once
        Mockito.verify(jobService, times(1)).cancelBookingJob("1");

        // Verify that the jobService.cancelReservationJob() method is called once
        Mockito.verify(jobService, times(1)).cancelReservationJob("1");

        // Verify that the jobService.buildBookingJob() method is called once
        Mockito.verify(jobService, times(1)).buildBookingJob(1L);

        // Verify that the jobService.buildBookingJobTrigger() method is called once
        Mockito.verify(jobService, times(1)).buildBookingJobTrigger(bookingJobDetail, event.bookingDateTime());

        // Verify that the jobService.buildReservationJob() method is called once
        Mockito.verify(jobService, times(1)).buildReservationJob(1L);

        // Verify that the jobService.buildReservationJobTrigger() method is called once
        Mockito.verify(jobService, times(1)).buildReservationJobTrigger(reservationJobDetail, reservationExpiry);

        // Verify that the jobService.scheduleJob() method is called twice
        Mockito.verify(jobService, times(2)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }
}
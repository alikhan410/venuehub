package com.venuehub.jobservice.service;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;
import java.util.Set;

@Component
public class JobService {
    Logger LOGGER = LoggerFactory.getLogger(JobService.class);
    private final Scheduler scheduler;

    @Autowired
    public JobService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public JobDetail buildBookingJob(Long bookingId) {

        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("bookingId", bookingId);

        return JobBuilder.newJob(RemoveBookingService.class)
                .withIdentity(String.valueOf(bookingId), "remove-booking-job")
                .withDescription("handles removing expire bookings")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public JobDetail buildReservationJob(Long bookingId) {

        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("bookingId", bookingId);

        return JobBuilder.newJob(RemoveReservationService.class)
                .withIdentity(String.valueOf(bookingId), "reservation-removing-job")
                .withDescription("handles removing reservations")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public Trigger buildBookingJobTrigger(JobDetail jobDetail, String dateTime) {
//        LOGGER.info(dateTime);
//        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
//        LOGGER.info(String.valueOf(localDateTime));
//        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Karachi"));
//        LOGGER.info(String.valueOf(zonedDateTime));
//        Instant instant = zonedDateTime.toInstant();
//        LOGGER.info(String.valueOf(instant));
//        Date date = Date.from(instant);
//        LOGGER.info(String.valueOf(date));
//        LocalDateTime ldt = LocalDateTime.parse(dateTime);
//        ZonedDateTime zonedDateTime = ZonedDateTime.of(ldt, ZoneId.of("Asia/"))
//        Instant instant = ldt.toInstant((ZoneOffset) ZoneId.of("PTL"));
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        Date date = Date.from(localDateTime.atZone(ZoneId.of("Asia/Karachi")).toInstant());

        //For production
//        LocalDate bookingDate = dateTime.toLocalDate();
//        LocalTime bookingTime = dateTime.toLocalTime();
//        ZoneId zoneId = ZoneId.systemDefault();
//        ZonedDateTime startAt = ZonedDateTime.of(bookingDate, bookingTime, zoneId);

        //For Development
        Date dateNow = Date.from(Instant.now());
        long fiveMinutesLater = dateNow.getTime() + 300000; // 5 min in milliseconds
        Date fiveMinutesDate = new Date(fiveMinutesLater);

        Date justNow = Date.from(Instant.now());
        long oneSecondLater = justNow.getTime() + 5000; // 5 min in milliseconds
        Date oneSecondDate = new Date(oneSecondLater);

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "remove-booking-triggers")
                .withDescription("Trigger for remove booking job")
//                .startAt(oneSecondDate)
//                .startAt(fiveMinutesDate)
                .startAt(date)
//                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    public Trigger buildReservationJobTrigger(JobDetail jobDetail, String dateTime) {

        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        Date date = Date.from(localDateTime.atZone(ZoneId.of("Asia/Karachi")).toInstant());

        //Hold the date reserved for two days
        LocalDate today = LocalDate.now();
        LocalDate twoDaysLater = today.plusDays(2);
        Date futureDate = Date.from(twoDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());

        //For development
        Date dateNow = Date.from(Instant.now());
        long twoMinutesLater = dateNow.getTime() + 120000; // 5 min in milliseconds
        Date twoMinutesDate = new Date(twoMinutesLater);

        //For testing
        Date justNow = Date.from(Instant.now());
        long oneSecondLater = justNow.getTime() + 1000;
        Date oneSecondDate = new Date(oneSecondLater);

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "reservation-removing-triggers")
                .withDescription("Trigger for reservation removing")
//                .startAt(oneSecondDate)
//                .startAt(twoMinutesDate)
//                .startAt(futureDate)
                .startAt(date)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    public void cancelBookingJob(String jobName) throws SchedulerException {
        //jobName is equivalent to String.of(bookingId)
        JobKey myJobKey = null;
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals("remove-booking-job"));

        for (JobKey jobKey : jobKeys) {
            if (jobKey.getName().equals(jobName)) {
                myJobKey = jobKey;
            }
        }
        if (myJobKey == null) {
            LOGGER.info("myJobkey is null in cancelBookingJob");
            return;
        }
        scheduler.deleteJob(myJobKey);
    }

    public void cancelReservationJob(String jobName) throws SchedulerException {
        //jobName is equivalent to String.of(bookingId)
        JobKey myJobKey = null;
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals("reservation-removing-job"));

        for (JobKey jobKey : jobKeys) {
            if (jobKey.getName().equals(jobName)) {
                myJobKey = jobKey;
            }
        }
        if (myJobKey == null) {
            LOGGER.info("myJobkey is null in cancelReservationJob");
            return;
        }
        scheduler.deleteJob(myJobKey);
    }

    public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        scheduler.scheduleJob(jobDetail, trigger);
    }

}

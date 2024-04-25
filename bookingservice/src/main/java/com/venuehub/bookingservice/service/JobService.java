package com.venuehub.bookingservice.service;


import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Date;
import java.util.Set;

@Component
public class JobService {
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

    public Trigger buildBookingJobTrigger(JobDetail jobDetail, LocalDateTime dateTime) {

        LocalDate bookingDate = dateTime.toLocalDate();
        LocalTime bookingTime = dateTime.toLocalTime();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime startAt = ZonedDateTime.of(bookingDate, bookingTime, zoneId);

        Date dateNow = Date.from(Instant.now());
        long fiveMinutesLater = dateNow.getTime() + 300000; // 5 min in milliseconds
        Date fiveMinutesDate = new Date(fiveMinutesLater);

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "remove-booking-triggers")
                .withDescription("Trigger for remove booking job")
                .startAt(fiveMinutesDate)
//                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    public Trigger buildReservationJob(JobDetail jobDetail) {

        //Hold the date reserved for two days
        LocalDate today = LocalDate.now();
        LocalDate twoDaysLater = today.plusDays(2);
        Date futureDate = Date.from(twoDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());

        //For development
        Date dateNow = Date.from(Instant.now());
        long twoMinutesLater = dateNow.getTime() + 120000; // 5 min in milliseconds
        Date twoMinutesDate = new Date(twoMinutesLater);

        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "reservation-removing-triggers")
                .withDescription("Trigger for reservation removing")
                .startAt(twoMinutesDate)
//                .startAt(futureDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

    public void cancelBookingJob(String jobName) throws SchedulerException {
        JobKey myJobKey = null;
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals("remove-booking-job"));

        for (JobKey jobKey : jobKeys) {
            if (jobKey.getName().equals(jobName)) {
                myJobKey = jobKey;
            }
        }
        scheduler.deleteJob(myJobKey);
    }

    public void cancelReservationJob(String jobName) throws SchedulerException {
        JobKey myJobKey = null;
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.groupEquals("reservation-removing-job"));

        for (JobKey jobKey : jobKeys) {
            if (jobKey.getName().equals(jobName)) {
                myJobKey = jobKey;
            }
        }
        scheduler.deleteJob(myJobKey);
    }

    public void scheduleJob(JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        scheduler.scheduleJob(jobDetail, trigger);
    }

}

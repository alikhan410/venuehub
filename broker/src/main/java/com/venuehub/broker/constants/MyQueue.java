package com.venuehub.broker.constants;

//public enum MyQueue {
//    venueQueue,
//    orderQueue,
//    bookingQueue,
//    userQueue,
//
//    String _FOO = venueQueue.;
//
//
//}


public enum MyQueue {
    ;
    public static class Constants {
        public static final String BOOKING_CREATED_QUEUE_PAYMENT_SERVICE = "BOOKING_CREATED_QUEUE_PAYMENT_SERVICE";
        public static final String BOOKING_UPDATED_QUEUE_PAYMENT_SERVICE = "BOOKING_UPDATED_QUEUE_PAYMENT_SERVICE";
        public static final String BOOKING_CREATED_QUEUE_VENUE_SERVICE = "BOOKING_CREATED_QUEUE_VENUE_SERVICE";
        public static final String BOOKING_UPDATED_QUEUE_VENUE_SERVICE = "BOOKING_UPDATED_QUEUE_VENUE_SERVICE";
        public static final String BOOKING_UPDATED_QUEUE_BOOKING_SERVICE = "BOOKING_UPDATED_QUEUE_BOOKING_SERVICE";
        public static final String VENUE_CREATED_QUEUE_BOOKING_SERVICE = "VENUE_CREATED_QUEUE_BOOKING_SERVICE";
        public static final String VENUE_DELETED_QUEUE_BOOKING_SERVICE = "VENUE_DELETED_QUEUE_BOOKING_SERVICE";
        public static final String VENUE_UPDATED_QUEUE_BOOKING_SERVICE = "VENUE_UPDATED_QUEUE_BOOKING_SERVICE";
        public static final String JOB_SCHEDULING_QUEUE_JOB_SERVICE = "JOB_SCHEDULING_QUEUE_JOB_SERVICE";
        public static final String JOB_CANCELLING_QUEUE_JOB_SERVICE = "JOB_CANCELLING_QUEUE_JOB_SERVICE";
        public static final String BOOKING_UPDATED_QUEUE_JOB_SERVICE = "BOOKING_UPDATED_QUEUE_JOB_SERVICE";
    }
}
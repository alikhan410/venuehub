package com.venuehub.broker.event.booking;

import com.venuehub.broker.constants.BookingStatus;

import java.awt.print.Book;
import java.io.Serializable;

@BookingCreated
public record BookingCreatedEvent(Long bookingId, Long venueId, BookingStatus status, String username) {
}
//public class BookingCreatedEvent implements Serializable {
//
//    private Long bookingId;
//    private Long venueId;
//    private BookingStatus status;
//    public BookingCreatedEvent(){}
//    public BookingCreatedEvent(Long bookingId, Long venueId, BookingStatus status) {
//        this.bookingId = bookingId;
//        this.venueId = venueId;
//        this.status = status;
//    }
//    public Long getBookingId() {
//        return bookingId;
//    }
//
//    public Long getVenueId() {
//        return venueId;
//    }
//
//    public BookingStatus getStatus() {
//        return status;
//    }
//
//    @Override
//    public String toString() {
//        return "BookingCreatedEvent{" +
//                "bookingId=" + bookingId +
//                ", venueId=" + venueId +
//                ", status=" + status +
//                '}';
//    }
//}

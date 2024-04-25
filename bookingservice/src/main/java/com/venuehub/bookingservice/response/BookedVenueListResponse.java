package com.venuehub.bookingservice.response;

import com.venuehub.bookingservice.dto.BookedVenueDto;
import lombok.Data;

import java.util.List;


public record BookedVenueListResponse(List<BookedVenueDto> bookingList) {
}
//@Data
//public class BookedVenueListResponse {
//    private final List<BookedVenueDto> bookingList;
//
//    public BookedVenueListResponse(List<BookedVenueDto> bookingList){
//        this.bookingList = bookingList;
//    }
//}
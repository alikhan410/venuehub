package com.venuehub.venueservice.response;

import com.venuehub.venueservice.dto.BookedVenueDto;
import lombok.Data;

import java.util.List;

@Data
public class BookedVenueListResponse {
    private final List<BookedVenueDto> bookingList;

    public BookedVenueListResponse(List<BookedVenueDto> bookingList){
        this.bookingList = bookingList;
    }
}

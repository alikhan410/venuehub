package com.venuehub.venueservice.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.venueservice.model.Venue;



public record BookedVenueDto (BookingStatus status,@JsonBackReference Venue venue){
}

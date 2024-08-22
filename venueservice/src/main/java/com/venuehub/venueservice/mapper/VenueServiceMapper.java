package com.venuehub.venueservice.mapper;

import com.venuehub.venueservice.dto.BookingDto;
import com.venuehub.venueservice.dto.ImageDto;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.Image;
import com.venuehub.venueservice.model.Venue;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface VenueServiceMapper {

    List<BookingDto> bookingsToDtoList(List<Booking> bookings);

    BookingDto bookingToDto(Booking booking);

    List<VenueDto> venuesToDtoList(List<Venue> venues);

    ImageDto imageToDto(Image image);

    List<ImageDto> imagesToDtoList(List<Image> images);

    VenueDto venueToDto(Venue venue);


}

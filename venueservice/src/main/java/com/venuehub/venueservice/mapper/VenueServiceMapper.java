package com.venuehub.venueservice.mapper;

import com.venuehub.venueservice.dto.BookingDto;
import com.venuehub.venueservice.dto.ImageDto;
import com.venuehub.venueservice.dto.VenueDto;
import com.venuehub.venueservice.model.Booking;
import com.venuehub.venueservice.model.ImageData;
import com.venuehub.venueservice.model.Venue;
import org.mapstruct.Mapping;

import java.util.List;

@org.mapstruct.Mapper
public interface VenueServiceMapper {

    List<BookingDto> bookingsToDtoList(List<Booking> bookings);

    BookingDto bookingToDto(Booking booking);

    List<ImageDto> imageDataListToDtoList(List<ImageData> imageDataList);

    ImageDto imageDataToDto(ImageData imageData);

    List<VenueDto> venuesToDtoList(List<Venue> venues);

    @Mapping(source = "images", target = "imageDtos")
    VenueDto venueToDto(Venue venue);
}

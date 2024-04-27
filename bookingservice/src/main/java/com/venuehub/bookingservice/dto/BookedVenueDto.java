package com.venuehub.bookingservice.dto;


import com.venuehub.bookingservice.validator.NotNullNotBlank;
import com.venuehub.broker.constants.BookingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class BookedVenueDto {
    @Email(message = "Email can not be null or empty")
    private String email;

    @NotNullNotBlank(message = "Phone can not be null or blank")
    private String phone;

    private BookingStatus status;

    @NotNullNotBlank(message = "Date can not be null or empty")
    private String bookingDateTime;

    @Min(value = 0, message = "Invalid value")
    private int guests;

}

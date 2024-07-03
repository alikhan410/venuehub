package com.venuehub.bookingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venuehub.bookingservice.dto.BookingDto;
import com.venuehub.bookingservice.dto.BookingDateTimeDto;
import com.venuehub.bookingservice.model.Booking;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.response.BookingDateListResponse;
import com.venuehub.bookingservice.response.BookingResponse;
import com.venuehub.bookingservice.service.BookingService;
import com.venuehub.bookingservice.service.VenueService;
import com.venuehub.bookingservice.utils.JwtTestImpl;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.event.job.BookingJobSchedulingEvent;
import com.venuehub.broker.producer.booking.BookingCreatedProducer;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtTestImpl jwtTestImpl;
    @MockBean
    private RabbitTemplate rabbitTemplate;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private VenueService venueService;
    @Autowired
    private BookingCreatedProducer bookingCreatedProducer;
    @Autowired
    private BookingUpdatedProducer bookingUpdatedProducer;
//    @Autowired
//    private ObjectMapper mapper;

    //fields
    private Long bookingId;
    private Long venueId;
    private String email;
    private String phone;
    private String bookingDateTime;
    private BookingDto bookingDto;
    private Booking booking;
    private Venue venue;
    private int guests;
    private BookingStatus status;
    private String username;
    private String name;
    private int estimates;
    private String myJwt;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;
    @Captor
    private ArgumentCaptor<String> bookingDateArgumentCaptor;

    @BeforeEach
    void BeforeEach() {
        venueId = 1L;
        bookingId = 1L;
        username = "test_user";
        email = "ali@gmail.com";
        phone = "03178923162";
        estimates = 45000;
        guests = 50;
        name = "Saffron Venue";
        bookingDateTime = "2024-12-04T18:30:00";
        status = BookingStatus.RESERVED;

        myJwt = jwtTestImpl.generateJwt(username, "USER");

        bookingDto = new BookingDto(email, phone, status, bookingDateTime, guests);

        venue = new Venue(venueId, name, estimates, username);

        booking = Booking.builder()
                .id(bookingId)
                .bookingDateTime(bookingDateTime)
                .status(BookingStatus.RESERVED)
                .venue(venue)
                .bookingFee(estimates)
                .username(username)
                .phone(phone)
                .email(email)
                .guests(guests).build();

    }

    public String asJsonString(Object obj) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj); // Convert object to JSON string
    }

    @Nested
    class AddBooking {

        @Test
        void Expect_401_When_User_UnAuthorized() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            mvc.perform(MockMvcRequestBuilders.post("/bookings/venue/" + venueId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingDto)))
                    .andExpect(status().isUnauthorized());

        }

        @Test
        void Expect_404_When_Venue_is_Not_Found() throws Exception {
            long wrongVenueId = 2L;
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            mvc.perform(MockMvcRequestBuilders.post("/bookings/venue/" + wrongVenueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_400_When_Booking_is_UnAvailable() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            Mockito.when(bookingService.isBookingAvailable(
                    LocalDateTime.parse(bookingDto.bookingDateTime()),
                    venue.getBookings())
            ).thenReturn(Boolean.FALSE);

            mvc.perform(MockMvcRequestBuilders.post("/bookings/venue/" + venueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void Should_produce_Event_3_times() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            Mockito.when(bookingService.isBookingAvailable(
                    LocalDateTime.parse(bookingDto.bookingDateTime()),
                    venue.getBookings())
            ).thenReturn(Boolean.TRUE);

            Mockito.when(bookingService.addNewBooking(bookingDto, venue, username)).thenReturn(booking);
            mvc.perform(MockMvcRequestBuilders.post("/bookings/venue/" + venueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingDto)))
                    .andExpect(status().isCreated());

            BookingCreatedEvent event = new BookingCreatedEvent(bookingId, venueId, BookingStatus.RESERVED, booking.getBookingFee(), username);
            BookingJobSchedulingEvent schedulingEvent = new BookingJobSchedulingEvent(bookingId, BookingStatus.RESERVED, bookingDateTime, booking.getReservationExpiry(), username);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.VENUE_EXCHANGE.name(), "booking-created", event);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.PAYMENT_EXCHANGE.name(), "booking-created", event);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.JOB_EXCHANGE.name(), "booking-job-scheduling", schedulingEvent);
        }

        @Test
        void Should_Add_Booking() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            Mockito.when(bookingService.isBookingAvailable(
                    LocalDateTime.parse(bookingDto.bookingDateTime()),
                    venue.getBookings())
            ).thenReturn(Boolean.TRUE);

            Mockito.when(bookingService.addNewBooking(bookingDto, venue, username)).thenReturn(booking);
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/bookings/venue/" + venueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingDto)))
                    .andExpect(status().isCreated()).andReturn();

            String response = result.getResponse().getContentAsString();
            BookingDto responseObject = new ObjectMapper().readValue(response, BookingDto.class);
            assertThat(responseObject.status().name()).isEqualTo(BookingStatus.RESERVED.name());
            assertThat(responseObject.bookingDateTime()).contains(bookingDateTime);
            assertThat(responseObject.email()).contains(email);
            assertThat(responseObject.phone()).contains(phone);

        }
    }

    @Nested
    class GetBookingByVenue {
        @Test
        void Expect_404_When_Venue_Not_Found() throws Exception {

            long wrongVenueId = 3L;

            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders.get("/bookings/venue/" + wrongVenueId))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

        @Test
        void Should_Return_List_Of_Bookings() throws Exception {
            List<Booking> bookings = new ArrayList<>();
            bookings.add(booking);
            venue.setBookings(bookings);
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));
            Mockito.when(bookingService.findByVenue(venueId)).thenReturn(bookings);

            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders.get("/bookings/venue/" + venueId))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = result.getResponse().getContentAsString();
            BookingDateListResponse responseObject = new ObjectMapper().readValue(response, BookingDateListResponse.class);

        }
    }

    @Nested
    class getBookingStatus {
        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {
            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders
                                    .get("/bookings/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                    )
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

        @Test
        void Should_Return_Booking() throws Exception {
            Mockito.when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));

            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders
                                    .get("/bookings/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
            String response = result.getResponse().getContentAsString();
            BookingResponse responseObject = new ObjectMapper().readValue(response, BookingResponse.class);


        }
    }

    @Nested
    class DeleteBooking {
        @Test
        void Expect_401_When_User_UnAuthorized() throws Exception {
            mvc.perform(MockMvcRequestBuilders.delete("/bookings/" + bookingId)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {
            Mockito.when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));
            long wrongBookingId = 5L;
            mvc.perform(MockMvcRequestBuilders.delete("/bookings/" + wrongBookingId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_403_When_Action_is_Forbidden() throws Exception {
            booking.setUsername("wrong_user");
            Mockito.when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));
            mvc.perform(MockMvcRequestBuilders.delete("/bookings/" + bookingId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden());
        }

        @Test
        void Should_Set_The_Booking_Failed() throws Exception {
            booking.setStatus(BookingStatus.BOOKED);
            Mockito.when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));
            Mockito.doNothing().when(bookingService).save(booking);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.delete("/bookings/" + bookingId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNoContent()).andReturn();

            Mockito.verify(bookingService, times(1)).save(bookingArgumentCaptor.capture());
            Booking updatedBooking = bookingArgumentCaptor.getValue();
            assertThat(updatedBooking.getStatus().name()).contains(BookingStatus.FAILED.name());

        }

        @Test
        void Should_Produce_Event_2_Times() throws Exception {
            booking.setStatus(BookingStatus.BOOKED);
            Mockito.when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));
            Mockito.doNothing().when(bookingService).save(booking);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.delete("/bookings/" + bookingId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNoContent()).andReturn();
            BookingUpdatedEvent event = new BookingUpdatedEvent(bookingId, BookingStatus.FAILED);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.VENUE_EXCHANGE.name(), "booking-updated", event);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.PAYMENT_EXCHANGE.name(), "booking-updated", event);
        }

    }

    @Nested
    class UpdateBookingDate {
        @Test
        void Except_401_When_User_UnAuthorized() throws Exception {
            mvc.perform(
                            MockMvcRequestBuilders.put("/bookings/" + bookingId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateTime)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {

            BookingDateTimeDto bookingDateTimeDto = new BookingDateTimeDto("2024-12-10T18:30:00");

            mvc.perform(
                            MockMvcRequestBuilders.put("/bookings/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateTimeDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_403_When_Action_Forbidden() throws Exception {

            BookingDateTimeDto bookingDateTimeDto = new BookingDateTimeDto("2024-12-10T18:30:00");

            booking.setUsername("wrong_user");

            Mockito.when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));

            mvc.perform(
                            MockMvcRequestBuilders.put("/bookings/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateTimeDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Should_Update_Booking_Date() throws Exception {
            //New Date
            BookingDateTimeDto bookingDateTimeDto = new BookingDateTimeDto("2024-12-10T18:30:00");

            booking.setStatus(BookingStatus.BOOKED);
            Mockito.when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));

            mvc.perform(
                            MockMvcRequestBuilders.put("/bookings/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateTimeDto)))
                    .andExpect(status().isNoContent());

            Mockito.verify(bookingService, times(1)).updateBooking(bookingArgumentCaptor.capture(), bookingDateArgumentCaptor.capture());
            Booking updatedBooking = bookingArgumentCaptor.getValue();
            String updatedBookingDate = bookingDateArgumentCaptor.getValue();

            assertThat(updatedBooking).isEqualTo(booking);
            assertThat(updatedBookingDate).isEqualTo(bookingDateTimeDto.bookingDateTime());
        }

        @Test
        void Should_Produce_Event_3_Times() throws Exception {
            //New Date
            BookingDateTimeDto bookingDateTimeDto = new BookingDateTimeDto("2024-12-10T18:30:00");
            booking.setStatus(BookingStatus.BOOKED);

            Mockito.when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));

            mvc.perform(
                            MockMvcRequestBuilders.put("/bookings/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateTimeDto)))
                    .andExpect(status().isNoContent());

            BookingUpdatedEvent event = new BookingUpdatedEvent(bookingId, BookingStatus.RESERVED);
            BookingJobSchedulingEvent schedulingEvent = new BookingJobSchedulingEvent(bookingId, BookingStatus.RESERVED, bookingDateTime, booking.getReservationExpiry(), username);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.VENUE_EXCHANGE.name(), "booking-updated", event);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.PAYMENT_EXCHANGE.name(), "booking-updated", event);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.JOB_EXCHANGE.name(), "booking-job-scheduling", schedulingEvent);
        }

    }
}
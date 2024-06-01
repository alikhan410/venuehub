package com.venuehub.bookingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venuehub.bookingservice.dto.BookedVenueDto;
import com.venuehub.bookingservice.dto.BookingDateDto;
import com.venuehub.bookingservice.model.BookedVenue;
import com.venuehub.bookingservice.model.Venue;
import com.venuehub.bookingservice.service.BookedVenueService;
import com.venuehub.bookingservice.service.VenueService;
import com.venuehub.bookingservice.utils.JwtTestImpl;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingCreatedEvent;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
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
@WebMvcTest(BookedVenueController.class)
class BookedVenueControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtTestImpl jwtTestImpl;
    @MockBean
    private RabbitTemplate rabbitTemplate;
    @MockBean
    private BookedVenueService bookedVenueService;
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
    private BookedVenueDto bookedVenueDto;
    private BookedVenue bookedVenue;
    private Venue venue;
    private int guests;
    private BookingStatus status;
    private String username;
    private String name;
    private String myJwt;
    @Captor
    private ArgumentCaptor<BookedVenue> bookedVenueArgumentCaptor;

    @BeforeEach
    void BeforeEach() {
        venueId = 1L;
        bookingId = 1L;
        username = "test_user";
        email = "ali@gmail.com";
        phone = "03178923162";
        guests = 50;
        name  = "Saffron Venue";
        bookingDateTime = "2024-12-04T18:30:00";
        status = BookingStatus.RESERVED;

        myJwt = jwtTestImpl.generateJwt(username);

        bookedVenueDto = new BookedVenueDto(email, phone, status, bookingDateTime, guests);

        venue = new Venue(venueId, name, username);

        bookedVenue = BookedVenue.builder()
                .id(bookingId)
                .bookingDateTime(bookingDateTime)
                .status(BookingStatus.RESERVED)
                .venue(venue)
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

            mvc.perform(MockMvcRequestBuilders.post("/booking/" + venueId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookedVenueDto)))
                    .andExpect(status().isUnauthorized());

        }

        @Test
        void Expect_404_When_Venue_is_Not_Found() throws Exception {
            long wrongVenueId = 2L;
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            mvc.perform(MockMvcRequestBuilders.post("/booking/" + wrongVenueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookedVenueDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_400_When_Booking_is_UnAvailable() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            Mockito.when(bookedVenueService.isBookingAvailable(
                    LocalDateTime.parse(bookedVenueDto.bookingDateTime()),
                    venue.getBookings())
            ).thenReturn(Boolean.FALSE);

            mvc.perform(MockMvcRequestBuilders.post("/booking/" + venueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookedVenueDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void Should_produce_Event_2_times() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            Mockito.when(bookedVenueService.isBookingAvailable(
                    LocalDateTime.parse(bookedVenueDto.bookingDateTime()),
                    venue.getBookings())
            ).thenReturn(Boolean.TRUE);

            Mockito.when(bookedVenueService.addNewBooking(bookedVenueDto, venue, username)).thenReturn(bookedVenue);
            mvc.perform(MockMvcRequestBuilders.post("/booking/" + venueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookedVenueDto)))
                    .andExpect(status().isCreated());

            BookingCreatedEvent event = new BookingCreatedEvent(bookingId, venueId, BookingStatus.RESERVED, username);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.VENUE_EXCHANGE.name(), "booking-created", event);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.PAYMENT_EXCHANGE.name(), "booking-created", event);
        }

        @Test
        void Should_Add_Booking() throws Exception {
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));

            Mockito.when(bookedVenueService.isBookingAvailable(
                    LocalDateTime.parse(bookedVenueDto.bookingDateTime()),
                    venue.getBookings())
            ).thenReturn(Boolean.TRUE);

            Mockito.when(bookedVenueService.addNewBooking(bookedVenueDto, venue, username)).thenReturn(bookedVenue);
            MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/booking/" + venueId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookedVenueDto)))
                    .andExpect(status().isCreated()).andReturn();

            String response = result.getResponse().getContentAsString();
            assertThat(response).contains(BookingStatus.RESERVED.name());
            assertThat(response).contains(bookingDateTime);
            assertThat(response).contains(email);
            assertThat(response).contains(phone);

        }
    }

    @Nested
    class GetBookingByVenue {
        @Test
        void Expect_404_When_Venue_Not_Found() throws Exception {

            long wrongVenueId = 3L;

            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders
                                    .get("/booking")
                                    .param("venue", String.valueOf(wrongVenueId)))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

        @Test
        void Should_Return_List_Of_Bookings() throws Exception {
            List<BookedVenue> bookings = new ArrayList<>();
            bookings.add(bookedVenue);
            venue.setBookings(bookings);
            Mockito.when(venueService.findById(venueId)).thenReturn(Optional.of(venue));
            Mockito.when(bookedVenueService.findByVenue(venueId)).thenReturn(bookings);

            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders
                                    .get("/booking")
                                    .param("venue", String.valueOf(venueId)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = result.getResponse().getContentAsString();
            assertThat(response).contains(BookingStatus.RESERVED.name());
            assertThat(response).contains(bookingDateTime);
            assertThat(response).contains(email);
            assertThat(response).contains(phone);
        }
    }

    @Nested
    class GetBookingById {
        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {
            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders
                                    .get("/booking/" + bookingId)
                    )
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

        @Test
        void Should_Return_Booking() throws Exception {
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));

            MvcResult result = mvc.perform(
                            MockMvcRequestBuilders
                                    .get("/booking/" + bookingId)
                    )
                    .andExpect(status().isOk())
                    .andReturn();
            String response = result.getResponse().getContentAsString();
            assertThat(response).contains(bookingId.toString());
            assertThat(response).contains(BookingStatus.RESERVED.name());
            assertThat(response).contains(bookingDateTime);
            assertThat(response).contains(email);
            assertThat(response).contains(phone);
            ;
        }
    }

    @Nested
    class DeleteBooking {
        @Test
        void Expect_401_When_User_UnAuthorized() throws Exception {
            mvc.perform(MockMvcRequestBuilders.delete("/booking/" + bookingId)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));
            long wrongBookingId = 5L;
            mvc.perform(MockMvcRequestBuilders.delete("/booking/" + wrongBookingId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_403_When_Action_is_Forbidden() throws Exception {
            bookedVenue.setUsername("wrong_user");
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));
            mvc.perform(MockMvcRequestBuilders.delete("/booking/" + bookingId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isForbidden());
        }

        @Test
        void Should_Set_The_Booking_Failed() throws Exception {
            bookedVenue.setStatus(BookingStatus.BOOKED);
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));
            Mockito.doNothing().when(bookedVenueService).save(bookedVenue);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.delete("/booking/" + bookingId)
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isNoContent()).andReturn();

            Mockito.verify(bookedVenueService, times(1)).save(bookedVenueArgumentCaptor.capture());
            BookedVenue updatedBooking = bookedVenueArgumentCaptor.getValue();
            assertThat(updatedBooking.getStatus().name()).contains(BookingStatus.FAILED.name());

        }

        @Test
        void Should_Produce_Event_2_Times() throws Exception {
            bookedVenue.setStatus(BookingStatus.BOOKED);
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));
            Mockito.doNothing().when(bookedVenueService).save(bookedVenue);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.delete("/booking/" + bookingId)
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
                            MockMvcRequestBuilders.put("/booking/" + bookingId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateTime)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {

            BookingDateDto bookingDateDto = new BookingDateDto("2024-12-10T18:30:00");

            mvc.perform(
                            MockMvcRequestBuilders.put("/booking/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_403_When_Action_Forbidden() throws Exception {

            BookingDateDto bookingDateDto = new BookingDateDto("2024-12-10T18:30:00");

            bookedVenue.setUsername("wrong_user");

            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));

            mvc.perform(
                            MockMvcRequestBuilders.put("/booking/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Should_Update_Booking_Date() throws Exception {
            //New Date
            BookingDateDto bookingDateDto = new BookingDateDto("2024-12-10T18:30:00");
            bookedVenue.setStatus(BookingStatus.BOOKED);
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));

            mvc.perform(
                            MockMvcRequestBuilders.put("/booking/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateDto)))
                    .andExpect(status().isNoContent());

            Mockito.verify(bookedVenueService, times(1)).save(bookedVenueArgumentCaptor.capture());
            BookedVenue updatedBookedVenue = bookedVenueArgumentCaptor.getValue();

            assertThat(updatedBookedVenue.getBookingDateTime()).isEqualTo(bookingDateDto.BookingDate());
        }

        @Test
        void Should_Produce_Event_2_Times() throws Exception {
            //New Date
            BookingDateDto bookingDateDto = new BookingDateDto("2024-12-10T18:30:00");
            bookedVenue.setStatus(BookingStatus.BOOKED);

            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));

            mvc.perform(
                            MockMvcRequestBuilders.put("/booking/" + bookingId)
                                    .header("Authorization", "Bearer " + myJwt)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(bookingDateDto)))
                    .andExpect(status().isNoContent());

            BookingUpdatedEvent event = new BookingUpdatedEvent(bookingId, BookingStatus.RESERVED);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.VENUE_EXCHANGE.name(), "booking-updated", event);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.PAYMENT_EXCHANGE.name(), "booking-updated", event);
        }

    }
}
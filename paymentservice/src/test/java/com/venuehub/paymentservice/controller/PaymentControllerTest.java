package com.venuehub.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.PaymentIntent;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import com.venuehub.paymentservice.dto.ConfirmPaymentDto;
import com.venuehub.paymentservice.dto.OrderDto;
import com.venuehub.paymentservice.model.BookedVenue;
import com.venuehub.paymentservice.service.BookedVenueService;
import com.venuehub.paymentservice.service.OrderService;
import com.venuehub.paymentservice.service.PaymentService;
import com.venuehub.paymentservice.utils.JwtTestImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;

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

import java.util.Optional;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentController.class)
class PaymentControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JwtTestImpl jwtTestImpl;
    @MockBean
    private RabbitTemplate rabbitTemplate;
    @MockBean
    private PaymentService paymentService;
    @MockBean
    private BookedVenueService bookedVenueService;
    @MockBean
    private OrderService OrderService;
    @Autowired
    private BookingUpdatedProducer producer;
    @Autowired
    private ObjectMapper mapper;

    //fields
    private int amount;
    private Long bookingId;
    private Long venueId;
    private String username;
    private String myJwt;
    private OrderDto orderDto;
    private String expectedClientSecret;
    private BookedVenue bookedVenue;

    @BeforeEach
    void BeforeEach() {
        bookingId = 2L;
        venueId = 1L;
        username = "test_user";
        myJwt = jwtTestImpl.generateJwt(username);
        amount = 250;
        expectedClientSecret = "secret";
        orderDto = new OrderDto(
                username,
                amount,
                bookingId
        );
        bookedVenue = new BookedVenue(
                bookingId,
                username,
                BookingStatus.RESERVED
        );
    }

    @Nested
    class CreatePaymentIntent {
        @Test
        void Expect_401_When_UnAuthenticated() throws Exception {

            MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/create-payment-intent")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(orderDto)))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        }

        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {
            Mockito.when(bookedVenueService.findById(5L)).thenReturn(Optional.of(bookedVenue));


            MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/create-payment-intent")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(orderDto)))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

        @Test
        void Expect_400_When_Action_is_Forbidden_1() throws Exception {
            OrderDto newOrderDto = new OrderDto(
                    "wrong_user",
                    amount,
                    bookingId
            );
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));


            MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/create-payment-intent")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(newOrderDto)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        @Test
        void Expect_400_When_Action_is_Forbidden_2() throws Exception {
            BookedVenue newBooking = new BookedVenue(bookingId, "wrong_user", BookingStatus.RESERVED);
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(newBooking));


            MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/create-payment-intent")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(orderDto)))
                    .andExpect(status().isForbidden())
                    .andReturn();
        }

        @Test
        void Successfully_Create_PaymentIntent() throws Exception {

            PaymentIntent paymentIntent = new PaymentIntent();
            paymentIntent.setClientSecret(expectedClientSecret);

            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));
            Mockito.when(paymentService.createPayment(amount)).thenReturn(paymentIntent);


            MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/create-payment-intent")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(orderDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            String response = result.getResponse().getContentAsString();
            assertThat(response).contains(expectedClientSecret);
            Mockito.verify(paymentService, times(1)).createPayment(250);

        }

    }

    @Nested
    class ConfirmPayment {

        @Test
        void Expect_401_When_Unauthorized() throws Exception {
            ConfirmPaymentDto confirmPaymentDto = new ConfirmPaymentDto("clientSecret", bookingId);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/confirm-payment")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(confirmPaymentDto)))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        }

        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {
            Mockito.when(bookedVenueService.findById(5L)).thenReturn(Optional.of(bookedVenue));
            ConfirmPaymentDto confirmPaymentDto = new ConfirmPaymentDto("clientSecret", bookingId);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/confirm-payment")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(confirmPaymentDto)))
                    .andExpect(status().isNotFound())
                    .andReturn();
        }

        @Test
        void Successfully_Confirm_Payment() throws Exception {
            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));
            ConfirmPaymentDto confirmPaymentDto = new ConfirmPaymentDto("clientSecret", bookingId);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/confirm-payment")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(confirmPaymentDto)))
                    .andExpect(status().isOk())
                    .andReturn();
            String response = result.getResponse().getContentAsString();
            assertThat(response).contains("Succeeded");
        }

        @Test
        void Should_Produce_Event_2_Times() throws Exception {

            Mockito.when(bookedVenueService.findById(bookingId)).thenReturn(Optional.of(bookedVenue));
            ConfirmPaymentDto confirmPaymentDto = new ConfirmPaymentDto("clientSecret", bookingId);

            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/confirm-payment")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(confirmPaymentDto)))
                    .andExpect(status().isOk())
                    .andReturn();

            BookingUpdatedEvent event = new BookingUpdatedEvent(bookingId, BookingStatus.BOOKED);

            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.VENUE_EXCHANGE.name(), "booking-updated", event);
            Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.BOOKING_EXCHANGE.name(), "booking-updated", event);
        }
    }


    public String asJsonString(Object obj) throws Exception {
        return mapper.writeValueAsString(obj); // Convert object to JSON string
    }
}
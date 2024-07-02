package com.venuehub.paymentservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.PaymentIntent;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import com.venuehub.paymentservice.dto.BookingIdDto;
import com.venuehub.paymentservice.dto.OrderDto;
import com.venuehub.paymentservice.model.Booking;
import com.venuehub.paymentservice.model.BookingOrder;
import com.venuehub.paymentservice.model.OrderStatus;
import com.venuehub.paymentservice.response.BookingOrderListResponse;
import com.venuehub.paymentservice.service.BookingService;
import com.venuehub.paymentservice.service.OrderService;
import com.venuehub.paymentservice.service.PaymentService;
import com.venuehub.paymentservice.utils.JwtTestImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockedStatic;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
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
    private BookingService bookingService;
    @MockBean
    private OrderService orderService;
    @Autowired
    private BookingUpdatedProducer producer;
    @Captor
    private ArgumentCaptor<BookingOrder> bookingOrderArgumentCaptor;
    @Autowired
    private ObjectMapper mapper;

    //fields
    private int amount;
    private Long bookingId;
    private Long orderId;
    private Long venueId;
    private String username;
    private String myJwt;
    private String expectedClientSecret;
    private String expectedClientId;
    private OrderDto orderDto;
    private Booking booking;
    private BookingOrder bookingOrder;
    private BookingIdDto bookingIdDto;

    @BeforeEach
    void BeforeEach() {
        bookingId = 2L;
        orderId = 1L;
        venueId = 1L;
        username = "test_user";
        expectedClientSecret = "expectedClientSecret";
        expectedClientId = "expectedClientId";
        myJwt = jwtTestImpl.generateJwt(username, "USER");
        amount = 250;
        orderDto = new OrderDto(
                orderId,
                username,
                expectedClientSecret,
                amount,
                bookingId,
                OrderStatus.PENDING
        );
        booking = new Booking(
                bookingId,
                username,
                amount,
                BookingStatus.RESERVED
        );

        bookingOrder = new BookingOrder(
                "user",
                amount,
                bookingId,
                expectedClientSecret
        );
        bookingIdDto = new BookingIdDto(bookingId);
    }

    @Nested
    class CreatePaymentIntent {
        @Test
        void Expect_401_When_UnAuthenticated() throws Exception {

            mvc.perform(MockMvcRequestBuilders.post("/orders/create-payment-intent")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_404_When_Booking_Not_Found() throws Exception {
            when(bookingService.findById(5L)).thenReturn(Optional.of(booking));


            mvc.perform(MockMvcRequestBuilders.post("/orders/create-payment-intent")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingIdDto)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_400_When_Person_is_Logged_in_as_Vendor() throws Exception {
            String wrongJwt = jwtTestImpl.generateJwt("vendor", "USER VENDOR", "VENDOR");

            mvc.perform(MockMvcRequestBuilders.post("/orders/create-payment-intent")
                            .header("Authorization", "Bearer " + wrongJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingIdDto)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Expect_400_When_Request_is_by_another_User() throws Exception {
            booking.setUsername("wrong_user");

            when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));


            mvc.perform(MockMvcRequestBuilders.post("/orders/create-payment-intent")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingIdDto)))
                    .andExpect(status().isForbidden());

        }

        @Test
        void Expect_400_When_Booking_is_Not_Reserved() throws Exception {
            booking.setStatus(BookingStatus.BOOKED);

            when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));


            mvc.perform(MockMvcRequestBuilders.post("/orders/create-payment-intent")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingIdDto)))
                    .andExpect(status().isForbidden());

        }

        @Test
        void Successfully_Create_PaymentIntent() throws Exception {
            PaymentIntent paymentIntent = new PaymentIntent();
            paymentIntent.setClientSecret(expectedClientSecret);

            when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));
            when(paymentService.createPayment(amount)).thenReturn(paymentIntent);


            mvc.perform(MockMvcRequestBuilders.post("/orders/create-payment-intent")
                            .header("Authorization", "Bearer " + myJwt)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(bookingIdDto)))
                    .andExpect(status().isCreated());


            Mockito.verify(paymentService, times(1)).createPayment(250);
            Mockito.verify(orderService, times(1)).save(bookingOrderArgumentCaptor.capture());

            BookingOrder savedBookingOrder = bookingOrderArgumentCaptor.getValue();

            assertEquals("test_user", savedBookingOrder.getUsername());
            assertEquals(amount, savedBookingOrder.getAmount());
            assertEquals(bookingId, savedBookingOrder.getBookingId());
            assertEquals(expectedClientSecret, savedBookingOrder.getClientSecret());

        }

    }

    @Nested
    class ConfirmPayment {

        @Test
        void Expect_401_When_Unauthorized() throws Exception {

            mvc.perform(MockMvcRequestBuilders.get("/orders/confirm-payment")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_403_When_Person_is_Logged_in_As_Vendor() throws Exception {
            String wrongJwt = jwtTestImpl.generateJwt("vendor", "USER VENDOR", "VENDOR");
            mvc.perform(MockMvcRequestBuilders.get("/orders/confirm-payment")
                    .header("Authorization", "Bearer " + wrongJwt)
                    .queryParam("clientId", "test")
                    .queryParam("clientSecret", "test")
                    .queryParam("vendor", "vendor")
            ).andExpect(status().isForbidden());
        }

        @Test
        void Expect_404_When_No_Order_is_Found() throws Exception {
            // Mock the order service method with wrong client secret
            when(orderService.findByClientSecret("wrongClientSecret")).thenReturn(Optional.of(bookingOrder));

            // Perform the request and assert the response
            mvc.perform(MockMvcRequestBuilders.get("/orders/confirm-payment")
                            .header("Authorization", "Bearer " + myJwt)
                            .queryParam("clientId", expectedClientId)
                            .queryParam("clientSecret", expectedClientSecret)
                            .queryParam("vendor", "vendor"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_400_When_Order_is_Completed() throws Exception {
            bookingOrder.setOrderStatus(OrderStatus.COMPLETED);
            when(orderService.findByClientSecret(expectedClientSecret)).thenReturn(Optional.of(bookingOrder));

            // Perform the request and assert the response
            mvc.perform(MockMvcRequestBuilders.get("/orders/confirm-payment")
                            .header("Authorization", "Bearer " + myJwt)
                            .queryParam("clientId", expectedClientId)
                            .queryParam("clientSecret", expectedClientSecret)
                            .queryParam("vendor", "vendor"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void Successfully_Confirm_Payment() throws Exception {
            PaymentIntent paymentIntent = new PaymentIntent();
            paymentIntent.setClientSecret(expectedClientSecret);
            paymentIntent.setStatus("succeeded");

            // Mock the static method
            try (MockedStatic<PaymentIntent> mockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
                mockedStatic.when(() -> PaymentIntent.retrieve(expectedClientId)).thenReturn(paymentIntent);

                // Mock the order service method
                when(orderService.findByClientSecret(expectedClientSecret)).thenReturn(Optional.of(bookingOrder));

                // Perform the request and assert the response
                mvc.perform(MockMvcRequestBuilders.get("/orders/confirm-payment")
                                .header("Authorization", "Bearer " + myJwt)
                                .queryParam("clientId", expectedClientId)
                                .queryParam("clientSecret", expectedClientSecret)
                                .queryParam("vendor", "vendor"))
                        .andExpect(status().isOk());
            }
        }

        @Test
        void Should_Produce_Event_2_Times() throws Exception {
            PaymentIntent paymentIntent = mock(PaymentIntent.class);

            // Mock the static method
            try (MockedStatic<PaymentIntent> mockedStatic = Mockito.mockStatic(PaymentIntent.class)) {
                mockedStatic.when(() -> PaymentIntent.retrieve(expectedClientId)).thenReturn(paymentIntent);

                // Mock methods on the mock PaymentIntent object
                when(paymentIntent.getClientSecret()).thenReturn(expectedClientSecret);
                when(paymentIntent.getStatus()).thenReturn("succeeded");

                // Mock the order service method
                when(orderService.findByClientSecret(expectedClientSecret)).thenReturn(Optional.of(bookingOrder));

                // Perform the request and assert the response
                mvc.perform(MockMvcRequestBuilders.get("/orders/confirm-payment")
                                .header("Authorization", "Bearer " + myJwt)
                                .queryParam("clientId", expectedClientId)
                                .queryParam("clientSecret", expectedClientSecret)
                                .queryParam("vendor", "vendor"))
                        .andExpect(status().isOk());

                BookingUpdatedEvent event = new BookingUpdatedEvent(bookingId, BookingStatus.BOOKED);

                Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.VENUE_EXCHANGE.name(), "booking-updated", event);
                Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.JOB_EXCHANGE.name(), "booking-updated", event);
                Mockito.verify(rabbitTemplate, times(1)).convertAndSend(MyExchange.BOOKING_EXCHANGE.name(), "booking-updated", event);

            }

        }

        @Test
        void When_Payment_is_Failed() {
            //TODO Test logic for when payment is failed
            assertEquals(4, 2);
        }
    }

    @Nested
    class GetOrderStatus {
        @Test
        void Expect_401_When_UnAuthenticated() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/orders/status/4"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_404_When_Order_is_Not_Found() throws Exception {

            mvc.perform(MockMvcRequestBuilders.get("/orders/status/4")
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_404_When_Booking_is_Not_Found() throws Exception {
            when(orderService.findById(orderId)).thenReturn(Optional.of(bookingOrder));

            mvc.perform(MockMvcRequestBuilders.get("/orders/status/" + orderId)
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isNotFound());
        }

        @Test
        void Expect_403_When_Booking_is_Not_equal_to_Reserved() throws Exception {
            when(orderService.findById(orderId)).thenReturn(Optional.of(bookingOrder));
            when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));

            booking.setStatus(BookingStatus.BOOKED);

            mvc.perform(MockMvcRequestBuilders.get("/orders/status/" + orderId)
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Successfully_retrieves_OrderStatus() throws Exception {
            when(orderService.findById(orderId)).thenReturn(Optional.of(bookingOrder));
            when(bookingService.findById(bookingId)).thenReturn(Optional.of(booking));

            mvc.perform(MockMvcRequestBuilders.get("/orders/status/" + orderId)
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class GetUserOrders {

        @Test
        void Expect_401_When_UnAuthenticated() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/orders/user"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_403_Person_is_Logged_in_As_Vendor() throws Exception {
            String wrongJwt = jwtTestImpl.generateJwt("vendor", "USER VENDOR", "VENDOR");

            mvc.perform(MockMvcRequestBuilders.get("/orders/user")
                            .header("Authorization", "Bearer " + wrongJwt))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Retrieves_Order_List_for_User() throws Exception {
            List<BookingOrder> bookingOrders = new ArrayList<>();
            bookingOrders.add(bookingOrder);
            when(orderService.findByUsername(username)).thenReturn(bookingOrders);
            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/orders/user")
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isOk()).andReturn();

            String content = result.getResponse().getContentAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            BookingOrderListResponse bookingOrderListResponse = objectMapper.readValue(content, BookingOrderListResponse.class);
            assertNotNull(bookingOrderListResponse.bookingOrders());
        }
    }

    @Nested
    class GetVendorOrders {

        @Test
        void Expect_401_When_UnAuthenticated() throws Exception {
            mvc.perform(MockMvcRequestBuilders.get("/orders/user"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void Expect_403_Person_is_Logged_in_As_User() throws Exception {

            mvc.perform(MockMvcRequestBuilders.get("/orders/vendor")
                            .header("Authorization", "Bearer " + myJwt))
                    .andExpect(status().isForbidden());
        }

        @Test
        void Retrieves_Order_List_for_Vendor() throws Exception {
            String correctJwt = jwtTestImpl.generateJwt("vendor", "USER VENDOR", "VENDOR");
            List<BookingOrder> bookingOrders = new ArrayList<>();

            bookingOrders.add(bookingOrder);
            when(orderService.findByVendor("vendor")).thenReturn(bookingOrders);
            MvcResult result = mvc.perform(MockMvcRequestBuilders.get("/orders/vendor")
                            .header("Authorization", "Bearer " + correctJwt))
                    .andExpect(status().isOk()).andReturn();

            String content = result.getResponse().getContentAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            BookingOrderListResponse bookingOrderListResponse = objectMapper.readValue(content, BookingOrderListResponse.class);
            assertNotNull(bookingOrderListResponse.bookingOrders());
        }

    }

    public String asJsonString(Object obj) throws Exception {
        return mapper.writeValueAsString(obj); // Convert object to JSON string
    }
}
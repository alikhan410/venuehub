package com.venuehub.paymentservice.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import com.venuehub.commons.exception.*;
import com.venuehub.paymentservice.dto.BookingDto;
import com.venuehub.paymentservice.dto.ConfirmPaymentDto;
import com.venuehub.paymentservice.dto.OrderDto;
import com.venuehub.paymentservice.dto.PaymentDto;
import com.venuehub.paymentservice.mapper.Mapper;
import com.venuehub.paymentservice.model.Booking;
import com.venuehub.paymentservice.model.BookingOrder;
import com.venuehub.paymentservice.model.OrderStatus;
import com.venuehub.paymentservice.response.ConfirmPaymentResponse;
import com.venuehub.paymentservice.response.CreatePaymentResponse;
import com.venuehub.paymentservice.service.BookedVenueService;
import com.venuehub.paymentservice.service.OrderService;
import com.venuehub.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@RestController
@Validated
public class PaymentController {
    private final PaymentService paymentService;
    private final BookedVenueService bookedVenueService;
    private final BookingUpdatedProducer producer;
    private final OrderService orderService;

    @Autowired
    public PaymentController(PaymentService paymentService, BookedVenueService bookedVenueService, BookingUpdatedProducer producer, OrderService orderService) {
        this.paymentService = paymentService;
        this.bookedVenueService = bookedVenueService;
        this.producer = producer;
        this.orderService = orderService;
    }

    @PostMapping("/orders/create-payment-intent")
    public ResponseEntity<CreatePaymentResponse> createPaymentIntent(@RequestBody BookingDto bookingDto, @AuthenticationPrincipal Jwt jwt) throws StripeException {
        Booking booking = bookedVenueService.findById(bookingDto.bookingId()).orElseThrow(NoSuchBookingException::new);

        if (!booking.getStatus().equals(BookingStatus.RESERVED)) throw new ActionForbiddenException();

        //TODO redo these exceptions
        if (!jwt.getSubject().equals(booking.getUsername())) throw new UserForbiddenException();

        PaymentIntent paymentIntent = paymentService.createPayment(booking.getBookingFee());

        BookingOrder bookingOrder = new BookingOrder(
                jwt.getSubject(),
                booking.getBookingFee(),
                booking.getId(),
                paymentIntent.getClientSecret()
        );

        orderService.save(bookingOrder);

        CreatePaymentResponse response = new CreatePaymentResponse(bookingOrder.getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<BookingOrder> getOrderStatus(@PathVariable Long orderId, @AuthenticationPrincipal Jwt jwt) {
        BookingOrder order = orderService.findById(orderId).orElseThrow(NoSuchOrderException::new);

        Booking booking = bookedVenueService.findById(order.getBookingId()).orElseThrow(NoSuchBookingException::new);

        if (!booking.getStatus().equals(BookingStatus.RESERVED)) throw new ActionForbiddenException("Reservation expired");

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

//    @GetMapping("/orders/confirm-payment")
//    @Transactional
//    public ConfirmPaymentResponse ConfirmPayment(@RequestBody ConfirmPaymentDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {
//        Booking booking = bookedVenueService.findById(body.bookingId()).orElseThrow(NoSuchBookingException::new);
//
//        if (!booking.getStatus().equals(BookingStatus.RESERVED)) {
//            //TODO make a new exception for booking not reserved
//            throw new BookingUnavailableException();
//        }
//
//        bookedVenueService.updateStatus(body.bookingId(), BookingStatus.BOOKED);
//        BookingOrder bookingOrder = orderService.findByBooking(booking.getId(), jwt.getSubject());
//        bookingOrder.setOrderStatus(OrderStatus.COMPLETED);
//        orderService.save(bookingOrder);
//
//        BookingUpdatedEvent event = new BookingUpdatedEvent(body.bookingId(), BookingStatus.BOOKED);
//        producer.produce(event, MyExchange.BOOKING_EXCHANGE);
//        producer.produce(event, MyExchange.VENUE_EXCHANGE);
//        producer.produce(event, MyExchange.JOB_EXCHANGE);
//        return new ConfirmPaymentResponse("Succeeded");
//
//    }


    @GetMapping("/orders/confirm-payment")
    @Transactional
    public ConfirmPaymentResponse ConfirmPayment(@RequestParam("clientId") String clientId,@RequestParam("clientSecret") String clientSecret, @AuthenticationPrincipal Jwt jwt) throws Exception {
//        Booking booking = bookedVenueService.findById(body.bookingId()).orElseThrow(NoSuchBookingException::new);
        BookingOrder order = orderService.findByClientSecret(clientSecret);

        if (order.getOrderStatus().equals(OrderStatus.COMPLETED)){
            throw new Exception("Order is already completed");
        }
//
//        if (!booking.getStatus().equals(BookingStatus.RESERVED)) {
//            //TODO make a new exception for booking not reserved
//            throw new BookingUnavailableException();
//        }

        PaymentIntent paymentIntent = PaymentIntent.retrieve(clientId);

        if (!paymentIntent.getStatus().equals("succeeded")) {

            order.setOrderStatus(OrderStatus.CANCELLED);
            orderService.save(order);

            BookingUpdatedEvent event = new BookingUpdatedEvent(order.getBookingId(), BookingStatus.FAILED);
            producer.produce(event, MyExchange.BOOKING_EXCHANGE);
            producer.produce(event, MyExchange.VENUE_EXCHANGE);
            producer.produce(event, MyExchange.JOB_EXCHANGE);

            throw new Exception("Payment did not succeed");

        };


        order.setOrderStatus(OrderStatus.COMPLETED);
        orderService.save(order);

        bookedVenueService.updateStatus(order.getBookingId(), BookingStatus.BOOKED);

        BookingUpdatedEvent event = new BookingUpdatedEvent(order.getBookingId(), BookingStatus.BOOKED);
        producer.produce(event, MyExchange.BOOKING_EXCHANGE);
        producer.produce(event, MyExchange.VENUE_EXCHANGE);
        producer.produce(event, MyExchange.JOB_EXCHANGE);

        return new ConfirmPaymentResponse("Succeeded");
    }
}

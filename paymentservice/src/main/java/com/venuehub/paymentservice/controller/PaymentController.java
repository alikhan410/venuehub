package com.venuehub.paymentservice.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import com.venuehub.commons.exception.BookingUnavailableException;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.commons.exception.UserForbiddenException;
import com.venuehub.paymentservice.dto.ConfirmPaymentDto;
import com.venuehub.paymentservice.dto.OrderDto;
import com.venuehub.paymentservice.dto.PaymentDto;
import com.venuehub.paymentservice.mapper.Mapper;
import com.venuehub.paymentservice.model.BookedVenue;
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

    @GetMapping("/order/create-payment-intent/{bookingId}")
    public ResponseEntity<OrderDto> createPaymentIntent(@PathVariable Long bookingId, @AuthenticationPrincipal Jwt jwt) throws StripeException {

        BookedVenue booking = bookedVenueService.findById(bookingId).orElseThrow(NoSuchBookingException::new);

        //TODO redo these exceptions
        if (!jwt.getSubject().equals(booking.getUsername())) throw new UserForbiddenException();
        if (!booking.getStatus().equals(BookingStatus.RESERVED)) throw new BookingUnavailableException();

        PaymentIntent paymentIntent = paymentService.createPayment(booking.getBookingFee());

        BookingOrder bookingOrder = new BookingOrder(
                jwt.getSubject(),
                booking.getBookingFee(),
                booking.getId(),
                paymentIntent.getClientSecret()
        );

        orderService.save(bookingOrder);

        OrderDto dto = Mapper.modelToDto(bookingOrder);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/order/client-secret/{clientSecret}")
    public ResponseEntity<BookingStatus> getOrderStatus(@PathVariable String clientSecret, @AuthenticationPrincipal Jwt jwt) {
        System.out.println(clientSecret);
        BookingOrder order = orderService.findByClientSecret(clientSecret);
        BookedVenue booking = bookedVenueService.findById(order.getBookingId()).orElseThrow(NoSuchBookingException::new);

        return new ResponseEntity<>(booking.getStatus(), HttpStatus.OK);
    }

    @GetMapping("/order/confirm-payment")
    @Transactional
    public ConfirmPaymentResponse ConfirmPayment(@RequestBody ConfirmPaymentDto body, @AuthenticationPrincipal Jwt jwt) throws Exception {
        BookedVenue booking = bookedVenueService.findById(body.bookingId()).orElseThrow(NoSuchBookingException::new);

        if (!booking.getStatus().equals(BookingStatus.RESERVED)) {
            //TODO make a new exception for booking not reserved
            throw new BookingUnavailableException();
        }

        bookedVenueService.updateStatus(body.bookingId(), BookingStatus.BOOKED);
        BookingOrder bookingOrder = orderService.findByBooking(booking.getId(), jwt.getSubject());
        bookingOrder.setOrderStatus(OrderStatus.COMPLETED);
        orderService.save(bookingOrder);

        BookingUpdatedEvent event = new BookingUpdatedEvent(body.bookingId(), BookingStatus.BOOKED);
        producer.produce(event, MyExchange.BOOKING_EXCHANGE);
        producer.produce(event, MyExchange.VENUE_EXCHANGE);
        producer.produce(event, MyExchange.JOB_EXCHANGE);
        return new ConfirmPaymentResponse("Succeeded");

    }


//    @GetMapping("/confirm-payment")
//    @Transactional
//    public ConfirmPaymentResponse ConfirmPayment(@RequestBody ConfirmPaymentDto body) throws Exception {
//        BookedVenue booking = bookedVenueService.findById(body.bookingId()).orElseThrow(NoSuchBookingException::new);
//        BookingOrder order = orderService.findByBooking(booking.getId());
//
//        if (!booking.getStatus().equals(BookingStatus.RESERVED)) {
//            //TODO make a new exception for booking not reserved
//            throw new BookingUnavailableException();
//        }
//
//        PaymentIntent paymentIntent = PaymentIntent.retrieve(body.clientId());
//
//        if (!paymentIntent.getStatus().equals("succeeded")) {
//
//            order.setOrderStatus(OrderStatus.CANCELLED);
//            orderService.save(order);
//
//            BookingUpdatedEvent event = new BookingUpdatedEvent(body.bookingId(), BookingStatus.FAILED);
//            producer.produce(event, MyExchange.BOOKING_EXCHANGE);
//            producer.produce(event, MyExchange.VENUE_EXCHANGE);
//            producer.produce(event, MyExchange.JOB_EXCHANGE);
//
//            throw new Exception("Payment did not succeed");
//
//        };
//
//
//        order.setOrderStatus(OrderStatus.PAID);
//        orderService.save(order);
//
//        bookedVenueService.updateStatus(body.bookingId(), BookingStatus.BOOKED);
//
//        BookingUpdatedEvent event = new BookingUpdatedEvent(body.bookingId(), BookingStatus.BOOKED);
//        producer.produce(event, MyExchange.BOOKING_EXCHANGE);
//        producer.produce(event, MyExchange.VENUE_EXCHANGE);
//        producer.produce(event, MyExchange.JOB_EXCHANGE);
//
//        return new ConfirmPaymentResponse("Succeeded");
//    }
}

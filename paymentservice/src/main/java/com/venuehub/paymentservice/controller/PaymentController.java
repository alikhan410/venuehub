package com.venuehub.paymentservice.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.constants.MyExchange;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.booking.BookingUpdatedProducer;
import com.venuehub.commons.exception.*;
import com.venuehub.paymentservice.dto.BookingIdDto;
import com.venuehub.paymentservice.model.Booking;
import com.venuehub.paymentservice.model.BookingOrder;
import com.venuehub.paymentservice.model.OrderStatus;
import com.venuehub.paymentservice.response.BookingOrderListResponse;
import com.venuehub.paymentservice.response.ConfirmPaymentResponse;
import com.venuehub.paymentservice.response.CreatePaymentResponse;
import com.venuehub.paymentservice.service.BookingService;
import com.venuehub.paymentservice.service.OrderService;
import com.venuehub.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class PaymentController {
    public static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final BookingUpdatedProducer producer;
    private final OrderService orderService;

    @Autowired
    public PaymentController(PaymentService paymentService, BookingService bookingService, BookingUpdatedProducer producer, OrderService orderService) {
        this.paymentService = paymentService;
        this.bookingService = bookingService;
        this.producer = producer;
        this.orderService = orderService;
    }

    @PostMapping("/orders/create-payment-intent")
    public ResponseEntity<CreatePaymentResponse> createPaymentIntent(@RequestBody BookingIdDto bookingIdDto, @AuthenticationPrincipal Jwt jwt) throws StripeException {
        if (!jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException();
        }

        Booking booking = bookingService.findById(bookingIdDto.bookingId()).orElseThrow(NoSuchBookingException::new);

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

    @GetMapping("/orders/status/{orderId}")
    public ResponseEntity<BookingOrder> getOrderStatus(@PathVariable Long orderId, @AuthenticationPrincipal Jwt jwt) {
        BookingOrder order = orderService.findById(orderId).orElseThrow(NoSuchOrderException::new);
        Booking booking = bookingService.findById(order.getBookingId()).orElseThrow(NoSuchBookingException::new);
        if (!booking.getStatus().equals(BookingStatus.RESERVED))
            throw new ActionForbiddenException("Reservation expired");
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/orders/user")
    public ResponseEntity<BookingOrderListResponse> getUserOrders(@AuthenticationPrincipal Jwt jwt) {
        if (!jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException();
        }
        List<BookingOrder> order = orderService.findByUsername(jwt.getSubject());
        BookingOrderListResponse res = new BookingOrderListResponse(order);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/orders/vendor")
    public ResponseEntity<BookingOrderListResponse> getVendorOrders(@AuthenticationPrincipal Jwt jwt) {
        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }
        List<BookingOrder> order = orderService.findByVendor(jwt.getSubject());
        BookingOrderListResponse res = new BookingOrderListResponse(order);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/orders/confirm-payment")
    @Transactional
    public ConfirmPaymentResponse ConfirmPayment(@RequestParam("clientId") String clientId, @RequestParam("clientSecret") String clientSecret, @RequestParam("vendor") String vendor, @AuthenticationPrincipal Jwt jwt) throws Exception {
        if (!jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException();
        }

        BookingOrder order = orderService.findByClientSecret(clientSecret).orElseThrow(NoSuchOrderException::new);

        if (order.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            throw new InvalidOrderStatusException("Order is already completed");
        }

        PaymentIntent paymentIntent = PaymentIntent.retrieve(clientId);
        if (!paymentIntent.getStatus().equals("succeeded")) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            order.setVendor(vendor);
            orderService.save(order);

            BookingUpdatedEvent event = new BookingUpdatedEvent(order.getBookingId(), BookingStatus.FAILED);
            producer.produce(event, MyExchange.BOOKING_EXCHANGE);
            producer.produce(event, MyExchange.VENUE_EXCHANGE);
            producer.produce(event, MyExchange.JOB_EXCHANGE);

            throw new Exception("Payment did not succeed");
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setVendor(vendor);
        orderService.save(order);
        bookingService.updateStatus(order.getBookingId(), BookingStatus.BOOKED);

        BookingUpdatedEvent event = new BookingUpdatedEvent(order.getBookingId(), BookingStatus.BOOKED);
        producer.produce(event, MyExchange.BOOKING_EXCHANGE);
        producer.produce(event, MyExchange.VENUE_EXCHANGE);
        producer.produce(event, MyExchange.JOB_EXCHANGE);

        return new ConfirmPaymentResponse("Succeeded");
    }
}

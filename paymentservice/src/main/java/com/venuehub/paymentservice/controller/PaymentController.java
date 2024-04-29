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
import com.venuehub.commons.exception.UserUnAuthorizedException;
import com.venuehub.paymentservice.dto.ConfirmPaymentDto;
import com.venuehub.paymentservice.dto.OrderDto;
import com.venuehub.paymentservice.model.BookedVenue;
import com.venuehub.paymentservice.response.ConfirmPaymentResponse;
import com.venuehub.paymentservice.response.CreatePaymentResponse;
import com.venuehub.paymentservice.service.BookedVenueService;
import com.venuehub.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class PaymentController {
    private final PaymentService paymentService;
    private final BookedVenueService bookedVenueService;
    private final BookingUpdatedProducer producer;

    @Autowired
    public PaymentController(PaymentService paymentService, BookedVenueService bookedVenueService, BookingUpdatedProducer producer) {
        this.paymentService = paymentService;
        this.bookedVenueService = bookedVenueService;
        this.producer = producer;
    }

    @PostMapping("/create-payment-intent")
    public CreatePaymentResponse checkout(@RequestBody OrderDto orderDto, @AuthenticationPrincipal Jwt jwt) throws StripeException {

        BookedVenue booking = bookedVenueService.findById(orderDto.bookingId()).orElseThrow(NoSuchBookingException::new);

        //TODO redo these exceptions
        if (!orderDto.username().equals(jwt.getSubject())) throw new UserForbiddenException();
        if (!orderDto.username().equals(booking.getUsername())) throw new UserForbiddenException();
        if (!booking.getStatus().equals(BookingStatus.RESERVED)) throw new BookingUnavailableException();

        PaymentIntent paymentIntent = paymentService.createPayment(orderDto.amount());
        return new CreatePaymentResponse(paymentIntent.getClientSecret());
    }

    @GetMapping("/confirm-payment")
    public ConfirmPaymentResponse ConfirmPayment(@RequestBody ConfirmPaymentDto body) throws Exception {
        BookedVenue booking = bookedVenueService.findById(body.bookingId()).orElseThrow(NoSuchBookingException::new);

        if(!booking.getStatus().equals(BookingStatus.RESERVED)){
            //TODO make a new exception for booking not reserved
            throw new BookingUnavailableException();
        }

        bookedVenueService.updateStatus(body.bookingId(), BookingStatus.BOOKED);

        BookingUpdatedEvent event = new BookingUpdatedEvent(body.bookingId(), BookingStatus.BOOKED);
        producer.produce(event, MyExchange.BOOKING_EXCHANGE);
        producer.produce(event, MyExchange.VENUE_EXCHANGE);
        return new ConfirmPaymentResponse("Succeeded");
//        PaymentIntent paymentIntent = PaymentIntent.retrieve(body.clientId());
//
//        if (paymentIntent.getStatus().equals("succeeded")) {
//            bookedVenueService.updateStatus(body.bookingId(), BookingStatus.BOOKED);
//
//            BookingUpdatedEvent event = new BookingUpdatedEvent(body.bookingId(), BookingStatus.BOOKED);
//            producer.produce(event);
//
//        } else {
//            throw new Exception("Payment did not succeed");
//        }

    }
}

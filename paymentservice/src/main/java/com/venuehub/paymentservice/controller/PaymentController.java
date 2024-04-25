package com.venuehub.paymentservice.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.venuehub.broker.constants.BookingStatus;
import com.venuehub.broker.event.booking.BookingUpdatedEvent;
import com.venuehub.broker.producer.BookingUpdatedProducer;
import com.venuehub.commons.exception.NoSuchBookingException;
import com.venuehub.commons.exception.UserUnAuthorizedException;
import com.venuehub.paymentservice.dto.BookedVenueDto;
import com.venuehub.paymentservice.dto.BookingPayment;
import com.venuehub.paymentservice.response.CreatePaymentResponse;
import com.venuehub.paymentservice.service.BookedVenueService;
import com.venuehub.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
    public CreatePaymentResponse checkout(@RequestBody BookedVenueDto bookedVenueDto, @AuthenticationPrincipal Jwt jwt) throws StripeException {
        bookedVenueService.findById(bookedVenueDto.id()).orElseThrow(NoSuchBookingException::new);

        if (bookedVenueDto.username() != jwt.getSubject()) throw new UserUnAuthorizedException();

        //TODO make a new exception of booking failed and add it here instead of user unauthorized exception
        if (bookedVenueDto.status()!=BookingStatus.RESERVED) throw new UserUnAuthorizedException();

        PaymentIntent paymentIntent = paymentService.createPayment(bookedVenueDto.amount());
        return new CreatePaymentResponse(paymentIntent.getClientSecret());
    }

    public void ConfirmPayment(String clientId, Long bookingId) throws Exception {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(clientId);

        if (paymentIntent.getStatus().equals("succeeded")) {
            bookedVenueService.updateStatus(bookingId, BookingStatus.BOOKED);

            BookingUpdatedEvent event = new BookingUpdatedEvent(bookingId, BookingStatus.BOOKED);
            producer.produce(event);

        } else {
            throw new Exception("Payment did not succeed");
        }

    }
}

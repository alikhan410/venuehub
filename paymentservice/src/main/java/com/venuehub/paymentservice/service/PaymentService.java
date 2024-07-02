package com.venuehub.paymentservice.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }


    public PaymentIntent createPayment(int amount) throws StripeException {


        PaymentIntentCreateParams.AutomaticPaymentMethods methods = PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                .setEnabled(true)
                .build();

        PaymentIntentCreateParams.Builder createParamsBuilder = PaymentIntentCreateParams.builder()
                .setAmount((long) amount)
                .setCurrency("PKR")
                .setAutomaticPaymentMethods(methods);


        return PaymentIntent.create(createParamsBuilder.build());

    }
}

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

//        public String capture(String paymentMethodId) throws StripeException {
//
//        PaymentIntent resource = PaymentIntent.retrieve("pi_3MrPBM2eZvKYlo2C1TEMacFD");
//        PaymentIntentCaptureParams params = PaymentIntentCaptureParams.builder().build();
//        PaymentIntent paymentIntent = resource.capture(params);
//        return paymentIntent.getStatus();
//    }


    //for pre-built solutions ie stripe hosted page or embedded stripe forms
//    public String createPayment() throws StripeException {
//
//        String YOUR_DOMAIN = "http://localhost:3000";
//        SessionCreateParams params =
//                SessionCreateParams.builder()
//                        .setUiMode(SessionCreateParams.UiMode.EMBEDDED)
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .setReturnUrl(YOUR_DOMAIN + "/return?session_id={CHECKOUT_SESSION_ID}")
//                        .addLineItem(
//                                SessionCreateParams.LineItem.builder()
//                                        .setQuantity(1L)
//                                        // Provide the exact Price ID (for example, pr_1234) of the product you want to sell
//                                        .setPrice("{{PRICE_ID}}")
//                                        .build())
//                        .build();
//
//        return Session.create(params).getClientSecret();
//
//    }
}

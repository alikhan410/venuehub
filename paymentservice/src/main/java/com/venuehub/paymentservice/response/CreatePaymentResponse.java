package com.venuehub.paymentservice.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatePaymentResponse {
    String clientSecret;
}

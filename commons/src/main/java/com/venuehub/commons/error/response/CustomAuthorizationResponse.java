package com.venuehub.commons.error.response;

import com.venuehub.commons.exception.ErrorResponse;
import org.springframework.http.HttpStatus;

public class CustomAuthorizationResponse extends ErrorResponse {
    public CustomAuthorizationResponse() {
        super(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, "Please login and try again");
    }
}

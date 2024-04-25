package com.venuehub.commons.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
public class ErrorResponse {

    private HttpStatus code;
    private String message;
    private List<ErrorDetails> details;

    public ErrorResponse(HttpStatus code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(HttpStatus code, String message, List<ErrorDetails> details) {
        this(code, message);
        this.details = details;
    }

    @Getter
    @Setter
    public static class ErrorDetails {
        private String field;
        private String error;

        public ErrorDetails(String field, String error) {
            this.error = error;
            this.field = field;
        }

    }

    public String toJsonString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting error response to JSON", e);
        }
    }
}


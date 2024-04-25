package com.venuehub.commons.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.util.List;


@Getter
public class NoSuchVenueException extends ResourceNotFoundException {
    private static final String field="Id";
    private static final String error = "No venues associated with this id";

    public NoSuchVenueException() {
        super(field, error);
    }
}

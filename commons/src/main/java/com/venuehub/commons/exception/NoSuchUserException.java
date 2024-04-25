package com.venuehub.commons.exception;

public class NoSuchUserException extends ResourceNotFoundException{
    private static final String field="Id";
    private static final String error = "No user associated with this id";

    public NoSuchUserException() {
        super(field, error); }
}

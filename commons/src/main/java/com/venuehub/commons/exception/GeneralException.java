package com.venuehub.commons.exception;

public class GeneralException extends InternalServerException{
    public GeneralException(String field, String error) {
        super(field, error);
    }
}

package com.venuehub.commons.exception;

public class UserUnAuthorizedException extends NotAuthorizedException {
    private static final String field="Id";
    private static final String error = "User is not authorized to view this info";

    public UserUnAuthorizedException() {
        super(field, error);
    }
}

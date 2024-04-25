package com.venuehub.commons.exception;

public class UserForbiddenException extends ForbiddenException {
    private static final String field="Id";
    private static final String error = "User is forbidden to perform this action";

    public UserForbiddenException() {
        super(field, error);
    }
}

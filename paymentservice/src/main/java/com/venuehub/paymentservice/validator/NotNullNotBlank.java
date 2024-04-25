package com.venuehub.paymentservice.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull
@NotBlank
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullNotBlank {
    String message() default "invalid input";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

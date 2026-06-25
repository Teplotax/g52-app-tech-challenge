package com.grupo52.tech_challenge.validation.annotation;

import com.grupo52.tech_challenge.validation.validator.SafeDtoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SafeDtoValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeDto {
    String message() default "O campo contém caracteres não permitidos";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
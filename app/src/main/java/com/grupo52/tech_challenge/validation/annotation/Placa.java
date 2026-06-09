package com.grupo52.tech_challenge.validation.annotation;

import com.grupo52.tech_challenge.validation.validator.PlacaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PlacaValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Placa {
    String message() default "Placa inválida, deve estar no formato ABC1234 ou ABC1D23";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
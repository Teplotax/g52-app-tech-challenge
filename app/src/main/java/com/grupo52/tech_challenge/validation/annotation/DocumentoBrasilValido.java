package com.grupo52.tech_challenge.validation.annotation;

import com.grupo52.tech_challenge.validation.validator.DocumentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DocumentValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentoBrasilValido {
    String message() default "documento: Documento inválido para o tipo informado";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
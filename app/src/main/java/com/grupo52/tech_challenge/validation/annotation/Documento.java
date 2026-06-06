package com.grupo52.tech_challenge.validation.annotation;

import com.grupo52.tech_challenge.validation.validator.DocumentoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DocumentoValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Documento {
    String message() default "Documento inválida, deve ser um CPF ou CNPJ válido, sem caracteres especiais";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
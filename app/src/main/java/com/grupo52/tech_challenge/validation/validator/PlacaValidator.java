package com.grupo52.tech_challenge.validation.validator;

import com.grupo52.tech_challenge.validation.annotation.Placa;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PlacaValidator implements ConstraintValidator<Placa, String> {

    private static final String PLACA_REGEX =
            "^(?:[A-Z]{3}[0-9]{4}|[A-Z]{3}[0-9][A-Z][0-9]{2})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // @NotBlank handles null
        }

        return value.toUpperCase().matches(PLACA_REGEX);
    }
}
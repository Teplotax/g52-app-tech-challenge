package com.grupo52.tech_challenge.validation.validator;

import com.grupo52.tech_challenge.validation.annotation.DocumentoBrasilValido;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

import java.lang.reflect.Method;

public class DocumentValidator implements ConstraintValidator<DocumentoBrasilValido, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            String tipo = getProperty(value, "getTipoDocumento");
            String doc = getProperty(value, "getDocumento");

            if ("CPF".equalsIgnoreCase(tipo)) {
                CPFValidator cpfValidator = new CPFValidator();
                cpfValidator.initialize(null);
                return cpfValidator.isValid(doc, context);
            } else if ("CNPJ".equalsIgnoreCase(tipo)) {
                CNPJValidator cnpjValidator = new CNPJValidator();
                cnpjValidator.initialize(null);
                return cnpjValidator.isValid(doc, context);
            }
        } catch (Exception e) {
            // Optionally log or handle error
            return false;
        }
        return false;
    }

    private String getProperty(Object obj, String getterName) throws Exception {
        Method method = obj.getClass().getMethod(getterName);
        Object result = method.invoke(obj);
        return result != null ? result.toString() : null;
    }
}
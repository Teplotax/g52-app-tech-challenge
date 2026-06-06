package com.grupo52.tech_challenge.validation.validator;

import com.grupo52.tech_challenge.validation.annotation.DocumentoBrasil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

import java.lang.reflect.Method;

public class DocumentValidator implements ConstraintValidator<DocumentoBrasil, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        try {
            String tipo = getProperty(value, "getTipoDocumento");
            String doc = getProperty(value, "getDocumento");

            if (tipo == null && doc == null) return true;

            if (doc == null || doc.isBlank()) return false;

            if ("CPF".equalsIgnoreCase(tipo)) {
                CPFValidator cpfValidator = new CPFValidator();
                cpfValidator.initialize(null);
                return cpfValidator.isValid(doc, context);
            } else if ("CNPJ".equalsIgnoreCase(tipo)) {
                return isValidCNPJ(doc);
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private String getProperty(Object obj, String getterName) throws Exception {
        Method method = obj.getClass().getMethod(getterName);
        Object result = method.invoke(obj);
        return result != null ? result.toString() : null;
    }

    private boolean isValidCNPJ(String cnpj) {
        // Strip formatting: dots, slashes, dashes
        String digits = cnpj.replaceAll("[.\\-/]", "");

        if (digits.length() != 14) return false;

        // Reject all-same-digit CNPJs (e.g. 00000000000000)
        if (digits.chars().distinct().count() == 1) return false;

        try {
            int[] nums = new int[14];
            for (int i = 0; i < 14; i++) {
                nums[i] = Character.getNumericValue(digits.charAt(i));
                if (nums[i] < 0) return false; // non-digit character
            }

            // First check digit
            int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int sum = 0;
            for (int i = 0; i < 12; i++) sum += nums[i] * weights1[i];
            int remainder = sum % 11;
            int d1 = remainder < 2 ? 0 : 11 - remainder;
            if (nums[12] != d1) return false;

            // Second check digit
            int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            sum = 0;
            for (int i = 0; i < 13; i++) sum += nums[i] * weights2[i];
            remainder = sum % 11;
            int d2 = remainder < 2 ? 0 : 11 - remainder;
            return nums[13] == d2;

        } catch (Exception e) {
            return false;
        }
    }
}
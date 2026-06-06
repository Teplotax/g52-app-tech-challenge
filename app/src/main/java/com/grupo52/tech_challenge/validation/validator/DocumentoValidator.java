package com.grupo52.tech_challenge.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.grupo52.tech_challenge.validation.annotation.Documento;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

public class DocumentoValidator implements ConstraintValidator<Documento, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true;

        String digits = value.replaceAll("[.\\-/]", "");

        if (digits.length() == 11) {
            return isValidCPF(digits, context);
        } else if (digits.length() == 14) {
            return isValidCNPJ(digits);
        }
        return false;
    }

    private boolean isValidCPF(String cpf, ConstraintValidatorContext context) {
        CPFValidator cpfValidator = new CPFValidator();
        cpfValidator.initialize(null);
        return cpfValidator.isValid(cpf, context);
    }

    private boolean isValidCNPJ(String cnpj) {
        if (cnpj.chars().distinct().count() == 1) return false;
        int[] nums = new int[14];
        for (int i = 0; i < 14; i++) nums[i] = cnpj.charAt(i) - '0';
        int[] weights1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int sum = 0;
        for (int i = 0; i < 12; i++) sum += nums[i] * weights1[i];
        int d1 = sum % 11 < 2 ? 0 : 11 - (sum % 11);
        if (nums[12] != d1) return false;
        int[] weights2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};
        sum = 0;
        for (int i = 0; i < 13; i++) sum += nums[i] * weights2[i];
        int d2 = sum % 11 < 2 ? 0 : 11 - (sum % 11);
        return nums[13] == d2;
    }
}
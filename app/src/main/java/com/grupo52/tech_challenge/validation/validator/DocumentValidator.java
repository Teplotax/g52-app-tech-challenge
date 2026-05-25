package com.grupo52.tech_challenge.validation.validator;

import com.grupo52.tech_challenge.dto.request.CreateClienteRequestDTO;
import com.grupo52.tech_challenge.validation.annotation.DocumentoBrasilValido;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;

public class DocumentValidator implements ConstraintValidator<DocumentoBrasilValido, CreateClienteRequestDTO> {

    @Override
    public boolean isValid(CreateClienteRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null) return true; // handled by @NotNull elsewhere

        String tipo = dto.getTipoDocumento();
        String doc = dto.getDocumento();

        if ("CPF".equalsIgnoreCase(tipo)) {
            CPFValidator cpfValidator = new CPFValidator();
            cpfValidator.initialize(null);
            return cpfValidator.isValid(doc, context);
        } else if ("CNPJ".equalsIgnoreCase(tipo)) {
            CNPJValidator cnpjValidator = new CNPJValidator();
            cnpjValidator.initialize(null);
            return cnpjValidator.isValid(doc, context);
        }
        return false;
    }
}
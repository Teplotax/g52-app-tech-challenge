package com.grupo52.tech_challenge.handler;

import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleBadRequest(final MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();

        // Field errors (e.g., telefone)
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        // Global errors (e.g., @DocumentoBrasilValido)
        for (ObjectError globalError : e.getBindingResult().getGlobalErrors()) {
            errors.add(globalError.getDefaultMessage());
        }

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ValidationErrorResponse(errors, "Invalid Request Body"));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<DefaultErrorMessage> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        String param = e.getName();
        String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        String value = e.getValue() != null ? e.getValue().toString() : "null";
        String message = String.format("'%s': Tipo de dado inválido para '%s'. Tipo de dado esperado: %s", param, value, requiredType);

        return ResponseEntity.status(BAD_REQUEST)
                .body(new DefaultErrorMessage(message, BAD_REQUEST.toString()));
    }

    @ExceptionHandler({GatewayException.class})
    public ResponseEntity<DefaultErrorMessage> handleGatewayException(final GatewayException e) {

        return ResponseEntity.status(HttpStatus.valueOf(e.getStatus())).body(
                new DefaultErrorMessage(e.getMessage(), HttpStatus.valueOf(e.getStatus()).toString()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<DefaultErrorMessage> handleException(final Exception e) {

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                new DefaultErrorMessage(e.getClass().getSimpleName() + ": " + e.getMessage(), "Internal Server Error"));
    }
}

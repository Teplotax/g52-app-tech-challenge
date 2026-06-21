package com.grupo52.tech_challenge.handler;

import com.grupo52.tech_challenge.exception.GatewayException;
import com.grupo52.tech_challenge.exception.InvalidStatusChangeException;
import com.grupo52.tech_challenge.exception.ValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleBadRequest(final MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        for (ObjectError globalError : e.getBindingResult().getGlobalErrors()) {
            errors.add(globalError.getDefaultMessage());
        }

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ValidationErrorResponse(errors, "Invalid Request Body"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<DefaultErrorMessage> handleConstraintViolation(final ConstraintViolationException e) {

        return ResponseEntity.status(BAD_REQUEST)
                .body(new DefaultErrorMessage(
                        e.getLocalizedMessage().substring(e.getLocalizedMessage().indexOf(":") + 2)
                        , BAD_REQUEST.toString()));
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

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<DefaultErrorMessage> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {

        String message = e.getLocalizedMessage();

        Pattern pattern = Pattern.compile(
                "Enums\\.([^`]+).*?from String \"([^\"]+)\".*?(: \\[(.*?)\\])"
        );

        Matcher matcher = pattern.matcher(e.getLocalizedMessage());

        if (matcher.find()) {
            String enumType = matcher.group(1);
            String invalidValue = matcher.group(2);
            String enumArray = matcher.group(3);

            message = String.format("'%s': Valor inválido para '%s'. Valores esperados: %s", enumType, invalidValue, enumArray);
        }

        return ResponseEntity.status(BAD_REQUEST)
                .body(new DefaultErrorMessage(message, BAD_REQUEST.toString()));
    }

    @ExceptionHandler({GatewayException.class})
    public ResponseEntity<DefaultErrorMessage> handleGatewayException(final GatewayException e) {

        return ResponseEntity.status(HttpStatus.valueOf(e.getStatus())).body(
                new DefaultErrorMessage(e.getMessage(), HttpStatus.valueOf(e.getStatus()).toString()));
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<DefaultErrorMessage> handleInvalidStatusChangeException(final ValidationException e) {

        return ResponseEntity.status(HttpStatus.valueOf(e.getStatus())).body(
                new DefaultErrorMessage(e.getMessage(), HttpStatus.valueOf(e.getStatus()).toString()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<DefaultErrorMessage> handleException(final Exception e) {

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                new DefaultErrorMessage(e.getClass().getSimpleName() + ": " + e.getMessage(), "Internal Server Error"));
    }
}
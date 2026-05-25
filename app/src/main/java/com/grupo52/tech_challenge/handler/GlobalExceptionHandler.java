package com.grupo52.tech_challenge.handler;

import com.grupo52.tech_challenge.exception.GatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ValidationErrorResponse> handleBadRequest(final MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + ": " + errorMessage);
        });
        return ResponseEntity.status(BAD_REQUEST).body(
                new ValidationErrorResponse(errors, "Invalid Request Body"));
    }

    @ExceptionHandler({GatewayException.class})
    public ResponseEntity<DefaultErrorMessage> handleGatewayException(final GatewayException e) {

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                new DefaultErrorMessage(e.getMessage(), HttpStatus.valueOf(e.getStatus()).toString()));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<DefaultErrorMessage> handleException(final Exception e) {

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                new DefaultErrorMessage(e.getMessage(), "Internal Server Error"));
    }
}

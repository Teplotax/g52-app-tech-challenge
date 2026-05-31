package com.grupo52.tech_challenge.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ValidationErrorResponse {
    private List<String> messages;
    private String exceptionType;
}

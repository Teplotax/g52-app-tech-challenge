package com.grupo52.tech_challenge.validation.validator;


import com.grupo52.tech_challenge.validation.annotation.SafeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SafeDtoValidator implements ConstraintValidator<SafeDto, Object> {

    private final SafeStringValidator stringValidator = new SafeStringValidator();

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        List<String> violations = new ArrayList<>();
        collectViolations(value, violations);

        if (violations.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        for (String fieldName : violations) {
            context.buildConstraintViolationWithTemplate(
                    "O campo contém caracteres não permitidos"
            ).addPropertyNode(fieldName).addConstraintViolation();
        }
        return false;
    }

    private void collectViolations(Object obj, List<String> violations) {
        Class<?> clazz = obj.getClass();
        while (clazz != null && !clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object fieldValue = field.get(obj);
                    if (fieldValue instanceof String str) {
                        if (!stringValidator.isValid(str, null)) {
                            violations.add(field.getName());
                        }
                    } else if (fieldValue instanceof List<?> list) {
                        for (Object item : list) {
                            if (item instanceof String str) {
                                if (!stringValidator.isValid(str, null)) {
                                    violations.add(field.getName());
                                    break;
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
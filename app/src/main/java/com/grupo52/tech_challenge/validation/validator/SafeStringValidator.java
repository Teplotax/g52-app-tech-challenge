package com.grupo52.tech_challenge.validation.validator;


import com.grupo52.tech_challenge.validation.annotation.SafeString;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SafeStringValidator implements ConstraintValidator<SafeString, String> {

    private static final Pattern BLOCKED = Pattern.compile(
            "(?i)" +
                    "<[^>]*>" +                          // HTML tags
                    "|<script|</script" +                // script tags
                    "|javascript:" +                     // JS protocol
                    "|on\\w+\\s*=" +                     // event handlers (onclick=, onerror=...)
                    "|'\\s*(or|and)\\s*'?\\d" +          // SQL: ' or '1
                    "|--\\s" +                           // SQL comment
                    "|;\\s*(drop|delete|insert|update|select|exec|union)\\b" + // SQL statements
                    "|\\.\\.\\/|\\.\\.\\\\" +            // path traversal
                    "|\\$\\{|#\\{" +                     // template injection (${...}, #{...})
                    "|\\\\x[0-9a-fA-F]{2}" +            // hex escape sequences
                    "|\\u0000"                           // null byte
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return !BLOCKED.matcher(value).find();
    }
}
package com.datsan.caulong.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ ElementType.TYPE })  // Áp dụng cho class, không phải field
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "Mật khẩu không khớp";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

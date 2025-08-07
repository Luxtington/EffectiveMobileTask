package ru.ivanov.Bank.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthdayYearValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBirthdayYear {
    String message() default "Год рождения должен быть не больше текущего и не менее 1925";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

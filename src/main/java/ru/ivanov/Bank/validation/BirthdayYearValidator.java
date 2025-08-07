package ru.ivanov.Bank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class BirthdayYearValidator implements ConstraintValidator<ValidBirthdayYear, Integer> {

    private final static int MIN_YEAR = 1925;
    private final static int CURRENT_YEAR = Year.now().getValue();

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext constraintValidatorContext) {
        return year >= MIN_YEAR && year <= CURRENT_YEAR;
    }
}

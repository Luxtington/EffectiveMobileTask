package ru.ivanov.Bank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class BirthdayYearValidator implements ConstraintValidator<ValidBirthdayYear, Integer> {

    private final static int MIN_YEAR = 1925;

    @Override
    public boolean isValid(Integer year, ConstraintValidatorContext constraintValidatorContext) {
        int CURRENT_YEAR = Year.now().getValue();
        return year >= MIN_YEAR && year <= CURRENT_YEAR;
    }
}

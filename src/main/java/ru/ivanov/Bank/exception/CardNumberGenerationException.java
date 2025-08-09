package ru.ivanov.Bank.exception;

public class CardNumberGenerationException extends RuntimeException {
    public CardNumberGenerationException(String message) {
        super(message);
    }
}

package ru.ivanov.Bank.exception;

public class TransactionTransferException extends RuntimeException {
    public TransactionTransferException(String message) {
        super(message);
    }
}

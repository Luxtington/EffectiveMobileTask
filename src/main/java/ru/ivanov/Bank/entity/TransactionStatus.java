package ru.ivanov.Bank.entity;

/**
 * Перечисление статусов банковских транзакций.
 * Определяет возможные статусы транзакций в банковской системе.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
public enum TransactionStatus {
    /**
     * Транзакция ожидает обработки.
     */
    PENDING(),
    
    /**
     * Транзакция успешно завершена.
     */
    COMPLETED(),
    
    /**
     * Транзакция отменена.
     */
    CANCELED()
}

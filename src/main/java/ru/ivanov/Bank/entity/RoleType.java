package ru.ivanov.Bank.entity;

/**
 * Перечисление типов ролей пользователей в системе.
 * Определяет возможные роли пользователей в банковской системе.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
public enum RoleType {
    /**
     * Обычный пользователь системы.
     */
    USER(),
    
    /**
     * Администратор системы.
     */
    ADMIN();

    /**
     * Возвращает строковое представление роли.
     * 
     * @return название роли
     */
    @Override
    public String toString() {
        return super.toString();
    }
}

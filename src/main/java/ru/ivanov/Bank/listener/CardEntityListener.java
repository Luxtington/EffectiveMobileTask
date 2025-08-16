package ru.ivanov.Bank.listener;

import jakarta.persistence.PostLoad;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.CardStatus;

import java.time.LocalDate;

/**
 * Класс для обновления статуса карты.
 * Предоставляет метод для обновления статуса карты
 * после загрузки сущности из БД.
 *
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */

/*TODO:
в дальнейшем можно этот класс убрать,
заменив, например, на Scheduled Task, чтобы статус обновлялся в фоновой задаче
 */

public class CardEntityListener {
    @PostLoad
    public void updateCardStatus(Card card) {
        if (card.getStatus() == CardStatus.ACTIVE && card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
        }
    }
}

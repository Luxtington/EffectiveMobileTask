package ru.ivanov.Bank.listener;

import jakarta.persistence.PostLoad;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.CardStatus;

import java.time.LocalDate;

public class CardEntityListener {
    @PostLoad
    public void updateCardStatus(Card card) {
        if (card.getStatus() == CardStatus.ACTIVE && card.getExpiryDate().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
        }
    }
}

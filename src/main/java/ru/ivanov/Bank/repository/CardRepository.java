package ru.ivanov.Bank.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.CardStatus;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByCardNumber(String number);
    Page<Card> findAllByStatus(CardStatus cardStatus, Pageable pageable);
}

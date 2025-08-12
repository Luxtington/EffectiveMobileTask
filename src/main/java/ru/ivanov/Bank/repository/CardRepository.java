package ru.ivanov.Bank.repository;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.CardStatus;
import ru.ivanov.Bank.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Page<Card> findAll(@Nullable Pageable pageable);
    Optional<Card> findByCardNumber(String number);
    Page<Card> findAllByStatus(CardStatus cardStatus, Pageable pageable);
    Page<Card> findByOwner(User user, Pageable pageable);
}

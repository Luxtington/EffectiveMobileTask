package ru.ivanov.Bank.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.Transaction;
import ru.ivanov.Bank.entity.TransactionStatus;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.fromCard.owner.id = :userId")
    Page<Transaction> findAllByUserId(@Param("userId") UUID id, Pageable pageable);
    @Query("SELECT t from Transaction t WHERE t.fromCard.id = :cardId")
    Page<Transaction> findAllByCardId(@Param("cardId") UUID id, Pageable pageable);
    Page<Transaction> findAllByStatus(TransactionStatus transactionStatus, Pageable pageable);
}

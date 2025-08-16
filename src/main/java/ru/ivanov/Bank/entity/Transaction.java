package ru.ivanov.Bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность банковской транзакции.
 * Представляет транзакцию между двумя картами с указанием суммы,
 * статуса и времени создания. Использует JPA для маппинга на таблицу "transaction".
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Entity
@ToString
@Setter
@Getter
@NoArgsConstructor
public class Transaction {
    /**
     * Уникальный идентификатор транзакции.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Карта отправителя.
     * Исключена из JSON сериализации для безопасности.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "from_card_id", referencedColumnName = "id", nullable = false)
    private Card fromCard;

    /**
     * Карта получателя.
     * Исключена из JSON сериализации для безопасности.
     */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "to_card_id", referencedColumnName = "id", nullable = false)
    private Card toCard;

    /**
     * Сумма транзакции.
     * Не может быть меньше 0.
     */
    @NotNull
    @DecimalMin(value = "0", message = "Сумма перевода не может быть меньше 0")
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * Статус транзакции.
     * По умолчанию PENDING.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    /**
     * Дата и время создания транзакции.
     * Автоматически устанавливается при создании записи.
     */
    @CreationTimestamp()
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Конструктор с параметрами для создания транзакции.
     * 
     * @param fromCard карта отправителя
     * @param toCard карта получателя
     * @param amount сумма транзакции
     */
    public Transaction(Card fromCard, Card toCard, BigDecimal amount) {
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
    }

    /**
     * Получает ID карты отправителя.
     * 
     * @return ID карты отправителя или null если карта не установлена
     */
    @Transient
    public UUID getFromCardId() {
        return fromCard != null ? fromCard.getId() : null;
    }

    /**
     * Получает ID карты получателя.
     * 
     * @return ID карты получателя или null если карта не установлена
     */
    @Transient
    public UUID getToCardId() {
        return toCard != null ? toCard.getId() : null;
    }
}

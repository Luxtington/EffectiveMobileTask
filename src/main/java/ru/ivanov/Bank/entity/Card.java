package ru.ivanov.Bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.ivanov.Bank.listener.CardEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сущность банковской карты.
 * Представляет банковскую карту с номером, балансом, статусом и связями
 * с пользователем и транзакциями. Использует JPA для маппинга на таблицу "card".
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Entity
@EntityListeners(CardEntityListener.class)
@ToString
@Setter
@Getter
@NoArgsConstructor
public class Card {
    /**
     * Уникальный идентификатор карты.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Номер карты.
     * Должен быть уникальным и содержать до 19 символов.
     */
    @Column(unique = true, nullable = false, length = 19)
    private String cardNumber;

    /**
     * Дата истечения срока действия карты.
     */
    @NotNull
    private LocalDate expiryDate;

    /**
     * Статус карты.
     * По умолчанию ACTIVE.
     */
    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.ACTIVE;

    /**
     * Баланс карты.
     * Не может быть меньше 0.
     */
    @NotNull
    @DecimalMin(value = "0", message = "Баланс карты не может быть меньше 0")
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Владелец карты.
     * Связь многие-к-одному с пользователем.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Исходящие транзакции с карты.
     * Исключены из JSON сериализации для безопасности.
     */
    @JsonIgnore
    @NotNull
    @OneToMany(mappedBy = "fromCard")
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    /**
     * Входящие транзакции на карту.
     * Исключены из JSON сериализации для безопасности.
     */
    @JsonIgnore
    @NotNull
    @OneToMany(mappedBy = "toCard")
    private List<Transaction> incomingTransactions = new ArrayList<>();

    /**
     * Конструктор с параметрами для создания карты.
     * 
     * @param cardNumber номер карты
     * @param expiryDate дата истечения срока действия
     * @param owner владелец карты
     */
    public Card(String cardNumber, LocalDate expiryDate, User owner) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.owner = owner;
    }

    /**
     * Получает все транзакции карты (входящие и исходящие).
     * 
     * @return список всех транзакций карты
     */
    @Transient
    public List<Transaction> getAllTransactions(){
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(outgoingTransactions);
        allTransactions.addAll(incomingTransactions);
        return allTransactions;
    }

    /**
     * Добавляет транзакцию как исходящую.
     * 
     * @param transaction транзакция для добавления
     */
    public void addTransactionAsOutgoing(@NotNull Transaction transaction){
        outgoingTransactions.add(transaction);
    }

    /**
     * Добавляет транзакцию как входящую.
     * 
     * @param transaction транзакция для добавления
     */
    public void addTransactionAsIncoming(@NotNull Transaction transaction){
        incomingTransactions.add(transaction);
    }

    /**
     * Проверяет, активна ли карта.
     * 
     * @return true если карта активна и не истек срок действия, false в противном случае
     */
    public Boolean isActive(){
        return status.equals(CardStatus.ACTIVE) && expiryDate.isAfter(LocalDate.now());
    }
}

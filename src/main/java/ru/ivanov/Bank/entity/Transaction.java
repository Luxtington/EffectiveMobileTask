package ru.ivanov.Bank.entity;

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

@Entity
@ToString
@Setter
@Getter
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "from_card_id", referencedColumnName = "id", nullable = false)
    private Card fromCard;

    @ManyToOne
    @JoinColumn(name = "to_card_id", referencedColumnName = "id", nullable = false)
    private Card toCard;

    @NotNull
    @DecimalMin(value = "0", message = "Сумма перевода не может быть меньше 0")
    private BigDecimal amount = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @CreationTimestamp()
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Transaction(Card fromCard, Card toCard, BigDecimal amount, TransactionStatus status) {
        this.fromCard = fromCard;
        this.toCard = toCard;
        this.amount = amount;
        this.status = status;
    }
}

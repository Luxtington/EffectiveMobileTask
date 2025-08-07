package ru.ivanov.Bank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@ToString
@Setter
@Getter
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 16)
    private String cardNumber;

    @NotNull
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.ACTIVE;

    @NotNull
    @DecimalMin(value = "0", message = "Баланс карты не может быть меньше 0")
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @NotNull
    @OneToMany(mappedBy = "fromCard")
    private List<Transaction> outgoingTransactions = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "toCard")
    private List<Transaction> incomingTransactions = new ArrayList<>();

    public Card(String cardNumber, LocalDate expiryDate, User owner) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.owner = owner;
    }

    @Transient
    public List<Transaction> getAllTransactions(){
        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(outgoingTransactions);
        allTransactions.addAll(incomingTransactions);
        return allTransactions;
    }

    public void addTransactionAsOutgoing(@NotNull Transaction transaction){
        outgoingTransactions.add(transaction);
    }

    public void addTransactionAsIncoming(@NotNull Transaction transaction){
        incomingTransactions.add(transaction);
    }
}

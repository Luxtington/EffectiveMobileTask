package ru.ivanov.Bank.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ivanov.Bank.entity.CardStatus;
import ru.ivanov.Bank.entity.Transaction;
import ru.ivanov.Bank.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseDto {
    private UUID id;
    private String cardNumber;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
    private User owner;
    private List<Transaction> outgoingTransactions = new ArrayList<>();
    private List<Transaction> incomingTransactions = new ArrayList<>();
}

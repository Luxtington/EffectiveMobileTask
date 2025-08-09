package ru.ivanov.Bank.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCardRequestDto {
    @NotNull
    private LocalDate expiryDate;

    @NotNull
    private UUID ownerId;
}

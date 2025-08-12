package ru.ivanov.Bank.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDto {
    @NotNull
    private UUID fromCardId;
    @NotNull
    private UUID toCardId;
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше 0")
    private BigDecimal amount;

    @AssertTrue(message = "Нельзя сделать перевод на карту с нее же самой")
    public boolean isDifferentCardsNumbers(){
        return !fromCardId.equals(toCardId);
    }

}

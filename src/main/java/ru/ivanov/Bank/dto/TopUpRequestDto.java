package ru.ivanov.Bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopUpRequestDto {
    @NotNull
    @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше 0")
    private BigDecimal amount;
    @NotBlank(message = "Номер телефона не может быть пустым")
    @Pattern(
            regexp = "^8-\\d{3}-\\d{3}-\\d{2}-\\d{2}$",
            message = "Номер телефона должен быть в формате: 8-XXX-XXX-XX-XX"
    )
    private String phoneNumber;
}

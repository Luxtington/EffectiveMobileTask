package ru.ivanov.Bank.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDto {
    @NotBlank(message = "Логин указывать обязательно")
    private String username;

    @NotBlank(message = "Пароль указывать обязательно")
    private String password;
}

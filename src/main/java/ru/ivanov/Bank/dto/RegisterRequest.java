package ru.ivanov.Bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ivanov.Bank.entity.Role;
import ru.ivanov.Bank.entity.RoleType;
import ru.ivanov.Bank.validation.ValidBirthdayYear;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotEmpty(message = "Фамилия не может быть пустой")
    @NotNull
    @Size(min = 1, max = 30, message = "Размер фамилии должен быть в диапазоне от 1 от до 30 символов")
    private String surname;

    @NotEmpty(message = "Имя не может быть пустым")
    @NotNull
    @Size(min = 2, max = 15, message = "Размер имени должен быть в диапазоне от 2 от до 15 символов")
    private String name;

    @Size(min = 0, max = 20, message = "Размер отчества должен быть в диапазоне от 5 от до 20 символов")
    private String patronymic;

    @ValidBirthdayYear
    private int birthdayYear;

    @NotEmpty(message = "Логин не может быть пустым")
    @NotNull
    @Size(min = 1, max = 30, message = "Размер логина должен быть в диапазоне от 1 от до 30 символов")
    private String username;

    @NotEmpty(message = "Пароль не может быть пустым")
    @NotNull
    @Size(min = 1, max = 100, message = "Размер пароля должен быть в диапазоне от 1 от до 30 символов")
    private String password;
} 
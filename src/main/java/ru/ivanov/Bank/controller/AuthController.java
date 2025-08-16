package ru.ivanov.Bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.Bank.dto.AuthRequestDto;
import ru.ivanov.Bank.dto.AuthResponseDto;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.service.AuthService;

import java.util.HashSet;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * Предоставляет REST API endpoints для входа в систему, регистрации
 * новых пользователей и изменения паролей.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Выполняет аутентификацию пользователя.
     * 
     * @param request данные для аутентификации (логин и пароль)
     * @return ответ с JWT токеном и информацией о пользователе или ошибка аутентификации
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {
        try {
            AuthResponseDto response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponseDto(null, null, new HashSet<>()));
        }
    }

    /**
     * Регистрирует нового пользователя в системе.
     * 
     * @param request данные для регистрации пользователя
     * @return ответ с JWT токеном и информацией о зарегистрированном пользователе или ошибка регистрации
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody CreateUserRequestDto request) {
        try {
            AuthResponseDto response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Throwable e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponseDto(null, null,  new HashSet<>()));
        }
    }

    /**
     * Изменяет пароль пользователя.
     * 
     * @param username логин пользователя
     * @param newPassword новый пароль
     * @return сообщение об успешном изменении пароля или ошибка
     */
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String username,
            @RequestParam String newPassword) {
        try {
            authService.changePassword(username, newPassword);
            return ResponseEntity.ok("Пароль успешно изменен");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Ошибка изменения пароля: " + e.getMessage());
        }
    }
} 
package ru.ivanov.Bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.Bank.dto.AuthRequest;
import ru.ivanov.Bank.dto.AuthResponse;
import ru.ivanov.Bank.dto.RegisterRequest;
import ru.ivanov.Bank.service.AuthService;

import java.util.HashSet;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, null, new HashSet<>()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerWithDetails(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Throwable e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, null,  new HashSet<>()));
        }
    }

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
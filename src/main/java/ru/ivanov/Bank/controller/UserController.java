package ru.ivanov.Bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.dto.UpdateUserRequestDto;
import ru.ivanov.Bank.dto.UserResponseDto;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.service.UserService;

import java.util.UUID;

/**
 * Контроллер для управления пользователями.
 * Предоставляет REST API endpoints для работы с пользователями:
 * получение списка, создание, обновление и удаление.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Получает список всех пользователей с пагинацией.
     * 
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return страница с пользователями
     */
    @GetMapping
    public ResponseEntity<Page<User>> getAll(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(userService.findAll(page, size));
    }

    /**
     * Получает пользователя по ID.
     * 
     * @param id уникальный идентификатор пользователя
     * @return найденный пользователь
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") UUID id){
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Создает нового пользователя.
     * 
     * @param requestDto данные для создания пользователя
     * @return созданный пользователь
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody CreateUserRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(requestDto));
    }

    /**
     * Обновляет существующего пользователя.
     * 
     * @param id уникальный идентификатор пользователя
     * @param requestDto данные для обновления пользователя
     * @return обновленный пользователь
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> edit(@PathVariable("id") UUID id,
                                                @Valid @RequestBody UpdateUserRequestDto requestDto){
        return ResponseEntity.ok(userService.save(requestDto, id));
    }

    /**
     * Удаляет пользователя по ID.
     * 
     * @param id уникальный идентификатор пользователя
     * @return ответ без содержимого
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

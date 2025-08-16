package ru.ivanov.Bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.Bank.dto.CardResponseDto;
import ru.ivanov.Bank.dto.CreateCardRequestDto;
import ru.ivanov.Bank.dto.TopUpRequestDto;
import ru.ivanov.Bank.service.CardService;

import java.security.Principal;
import java.util.UUID;

/**
 * Контроллер для управления банковскими картами.
 * Предоставляет REST API endpoints для работы с картами:
 * получение списка, создание, блокировка, пополнение баланса и удаление.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    /**
     * Получает список всех карт с пагинацией.
     * 
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return страница с картами
     */
    @GetMapping
    public ResponseEntity<Page<CardResponseDto>> getAll(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(cardService.findAll(page, size));
    }

    /**
     * Получает карту по ID.
     * 
     * @param id уникальный идентификатор карты
     * @return найденная карта
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> getById(@PathVariable("id") UUID id){
        return ResponseEntity.ok(cardService.findById(id));
    }

    /**
     * Получает карты пользователя с пагинацией.
     * 
     * @param ownerId уникальный идентификатор владельца карт
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return страница с картами пользователя
     */
    @GetMapping("/user")
    public ResponseEntity<Page<CardResponseDto>> getByOwnerId(@RequestParam UUID ownerId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(cardService.findByOwnerId(ownerId, page, size));

    }

    /**
     * Создает новую карту.
     * 
     * @param requestDto данные для создания карты
     * @return созданная карта
     */
    @PostMapping
    public ResponseEntity<CardResponseDto> create(@Valid @RequestBody CreateCardRequestDto requestDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.save(requestDto));
    }

    /**
     * Блокирует карту.
     * 
     * @param id уникальный идентификатор карты
     * @return заблокированная карта
     */
    @PatchMapping("block/{id}")
    public ResponseEntity<CardResponseDto> block(@PathVariable("id") UUID id){
        return ResponseEntity.ok(cardService.blockCard(id));
    }

    /**
     * Пополняет баланс карты.
     * 
     * @param id уникальный идентификатор карты
     * @param requestDto данные для пополнения баланса
     * @return карта с обновленным балансом
     */
    @PatchMapping("/{id}/top-up")
    public ResponseEntity<CardResponseDto> topUpBalance(@PathVariable("id") UUID id,
                                                        @Valid @RequestBody TopUpRequestDto requestDto,
                                                        Principal principal){
        return ResponseEntity.ok(cardService.topUpCardBalance(id, requestDto, principal.getName()));
    }

    /**
     * Удаляет карту по ID.
     * 
     * @param id уникальный идентификатор карты
     * @return ответ без содержимого
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
        cardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

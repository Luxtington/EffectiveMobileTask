package ru.ivanov.Bank.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ivanov.Bank.dto.TransferRequestDto;
import ru.ivanov.Bank.entity.Transaction;
import ru.ivanov.Bank.service.TransactionService;

import java.security.Principal;
import java.util.UUID;

/**
 * Контроллер для управления банковскими транзакциями.
 * Предоставляет REST API endpoints для работы с транзакциями:
 * получение списка, создание переводов и удаление.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    /**
     * Получает список всех транзакций с пагинацией.
     * 
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return страница с транзакциями
     */
    @GetMapping
    public ResponseEntity<Page<Transaction>> getAll(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(transactionService.findAll(page, size));
    }

    /**
     * Получает транзакцию по ID.
     * 
     * @param id уникальный идентификатор транзакции
     * @return найденная транзакция
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable("id") UUID id){
        return ResponseEntity.ok(transactionService.findById(id));
    }

    /**
     * Получает транзакции пользователя с пагинацией.
     * 
     * @param ownerId уникальный идентификатор пользователя
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return страница с транзакциями пользователя
     */
    @GetMapping("/user")
    public ResponseEntity<Page<Transaction>> getByOwnerId(@RequestParam UUID ownerId,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(transactionService.findAllByUserId(ownerId, page, size));
    }

    /**
     * Получает транзакции карты с пагинацией.
     * 
     * @param cardId уникальный идентификатор карты
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @return страница с транзакциями карты
     */
    @GetMapping("/card")
    public ResponseEntity<Page<Transaction>> getByCardId(@RequestParam UUID cardId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(transactionService.findAllByCardId(cardId, page, size));
    }

    /**
     * Создает перевод денежных средств между картами.
     * 
     * @param requestDto данные для перевода
     * @param principal информация об аутентифицированном пользователе
     * @return созданная транзакция
     */
    @PostMapping("/transfer")
    public ResponseEntity<Transaction> create(@Valid @RequestBody TransferRequestDto requestDto,
                                              Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.transferMoneyBetweenCards(requestDto, principal.getName()));
    }

    /**
     * Удаляет транзакцию по ID.
     * 
     * @param id уникальный идентификатор транзакции
     * @return ответ без содержимого
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id){
        transactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

package ru.ivanov.Bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.Bank.dto.TransferRequestDto;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.Transaction;
import ru.ivanov.Bank.entity.TransactionStatus;
import ru.ivanov.Bank.exception.CardNotFoundException;
import ru.ivanov.Bank.exception.TransactionNotFoundException;
import ru.ivanov.Bank.exception.TransactionTransferException;
import ru.ivanov.Bank.repository.CardRepository;
import ru.ivanov.Bank.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для управления банковскими транзакциями.
 * Предоставляет методы для работы с транзакциями: поиск, создание,
 * выполнение переводов между картами и удаление. Включает бизнес-логику
 * для проверки возможности переводов.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Находит все транзакции с пагинацией.
     * 
     * @param pageNumber номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с транзакциями
     */
    public Page<Transaction> findAll(int pageNumber, int size){
        Pageable pageable = PageRequest.of(pageNumber, size);
        return transactionRepository.findAll(pageable);
    }

    /**
     * Находит транзакцию по ID.
     * 
     * @param id уникальный идентификатор транзакции
     * @return найденная транзакция
     * @throws TransactionNotFoundException если транзакция не найдена
     */
    public Transaction findById(UUID id) throws TransactionNotFoundException{
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException("Транзакция с id = " + id + " не найдена"));
    }

    /**
     * Находит все транзакции пользователя с пагинацией.
     * 
     * @param userId уникальный идентификатор пользователя
     * @param pageNumber номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с транзакциями пользователя
     */
    public Page<Transaction> findAllByUserId(UUID userId, int pageNumber, int size){
        Pageable pageable = PageRequest.of(pageNumber, size);
        return transactionRepository.findAllByUserId(userId, pageable);
    }

    /**
     * Находит все транзакции карты с пагинацией.
     * 
     * @param cardId уникальный идентификатор карты
     * @param pageNumber номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с транзакциями карты
     */
    public Page<Transaction> findAllByCardId(UUID cardId, int pageNumber, int size){
        Pageable pageable = PageRequest.of(pageNumber, size);
        return transactionRepository.findAllByCardId(cardId, pageable);
    }

    /**
     * Выполняет перевод денежных средств между картами.
     * 
     * <p>Проверяет следующие условия:
     * <ul>
     *   <li>Карты принадлежат одному пользователю</li>
     *   <li>Операцию выполняет владелец карт</li>
     *   <li>Обе карты активны</li>
     *   <li>На карте отправителя достаточно средств</li>
     * </ul></p>
     * 
     * @param requestDto данные для перевода
     * @param transferAuthorUsername логин пользователя, выполняющего перевод
     * @return созданная транзакция
     * @throws CardNotFoundException если одна из карт не найдена
     * @throws TransactionTransferException если перевод невозможен по бизнес-правилам
     */
    @Transactional
    public Transaction transferMoneyBetweenCards(TransferRequestDto requestDto, String transferAuthorUsername) throws CardNotFoundException {
        Card fromCard = cardRepository.findById(requestDto.getFromCardId()).orElseThrow(() -> new CardNotFoundException("Карта с id = " + requestDto.getFromCardId() + " не найдена"));
        Card toCard = cardRepository.findById(requestDto.getToCardId()).orElseThrow(() -> new CardNotFoundException("Карта с id = " + requestDto.getToCardId() + " не найдена"));

        if (!fromCard.getOwner().getUsername().equals(transferAuthorUsername)){
            throw new TransactionTransferException("Только владелец карт может делать финансовые операции с ними");
        }

        if (!fromCard.getOwner().equals(toCard.getOwner())) {
            throw new TransactionTransferException("Перевод денежных средств может быть осуществлен только между картами одного пользователя");
        }

        if (!(fromCard.isActive() && toCard.isActive())) {
            throw new TransactionTransferException("Перевод денежных средств может быть осуществлен только между активными картами");
        }

        if (fromCard.getBalance().compareTo(requestDto.getAmount()) < 0){
            throw new TransactionTransferException("Недостаточно средств для перевода");
        }

        Transaction transaction = new Transaction(fromCard, toCard, requestDto.getAmount());

        try {
            BigDecimal newFromCardBalance = fromCard.getBalance().subtract(requestDto.getAmount());
            fromCard.setBalance(newFromCardBalance);

            BigDecimal newToCardBalance = toCard.getBalance().add(requestDto.getAmount());
            toCard.setBalance(newToCardBalance);

            cardRepository.save(fromCard);
            cardRepository.save(toCard);
            transaction.setStatus(TransactionStatus.COMPLETED);
        } catch (Exception e){
            transaction.setStatus(TransactionStatus.CANCELED);
            throw e;
        } finally {
            return transactionRepository.save(transaction);
        }
    }

    /**
     * Удаляет транзакцию по ID.
     * 
     * @param id уникальный идентификатор транзакции
     */
    @Transactional
    public void deleteById(UUID id){
        transactionRepository.deleteById(id);
    }
}

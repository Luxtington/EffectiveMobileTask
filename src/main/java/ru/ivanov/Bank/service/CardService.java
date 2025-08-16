package ru.ivanov.Bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.Bank.dto.CardResponseDto;
import ru.ivanov.Bank.dto.CreateCardRequestDto;
import ru.ivanov.Bank.dto.TopUpRequestDto;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.CardStatus;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.exception.CardNotFoundException;
import ru.ivanov.Bank.exception.TransactionTransferException;
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.mapper.CardMapper;
import ru.ivanov.Bank.repository.CardRepository;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.util.CardNumberGenerator;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сервис для управления банковскими картами.
 * Предоставляет методы для работы с картами: поиск, создание,
 * блокировка, пополнение баланса и удаление. Включает генерацию
 * номеров карт и маппинг данных.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardNumberGenerator cardNumberGenerator;

    /**
     * Находит все карты с пагинацией.
     * 
     * @param pageNumber номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с картами в формате DTO
     */
    public Page<CardResponseDto> findAll(int pageNumber, int size){
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Card> cardPage = cardRepository.findAll(pageable);
        return cardPage.map(cardMapper::toDtoFromCard);
    }

    /**
     * Находит карту по ID.
     * 
     * @param id уникальный идентификатор карты
     * @return найденная карта в формате DTO
     * @throws CardNotFoundException если карта не найдена
     */
    public CardResponseDto findById(UUID id) throws CardNotFoundException {
        Card card = cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Карта с id = " + id + " не найдена"));
        return cardMapper.toDtoFromCard(card);
    }

    /**
     * Находит карты пользователя с пагинацией.
     * 
     * @param ownerId уникальный идентификатор владельца карт
     * @param pageNumber номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с картами пользователя в формате DTO
     * @throws UserNotFoundException если пользователь не найден
     */
    public Page<CardResponseDto> findByOwnerId(UUID ownerId, int pageNumber, int size){
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + ownerId + " не найден"));
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Card> cards = cardRepository.findByOwner(owner, pageable);
        return cards.map(cardMapper::toDtoFromCard);
    }

    /**
     * Создает новую карту.
     * 
     * @param requestDto данные для создания карты
     * @return созданная карта в формате DTO
     * @throws UserNotFoundException если владелец карты не найден
     */
    @Transactional
    public CardResponseDto save(CreateCardRequestDto requestDto) throws UserNotFoundException{
        Card card = cardMapper.toCardFromCreateRequest(requestDto);
        card.setCardNumber(cardNumberGenerator.generate());
        card.setOwner(userRepository.findById(requestDto.getOwnerId()).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + requestDto.getOwnerId() + " не найден")));
        card = cardRepository.save(card);
        return cardMapper.toDtoFromCard(card);
    }

    /**
     * Блокирует карту.
     * 
     * @param id уникальный идентификатор карты
     * @return заблокированная карта в формате DTO
     * @throws CardNotFoundException если карта не найдена
     */
    @Transactional
    public CardResponseDto blockCard(UUID id) throws CardNotFoundException {
        Card card = cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Карта с id = " + id + " не найдена"));
        card.setStatus(CardStatus.BLOCKED);
        card = cardRepository.save(card);
        return cardMapper.toDtoFromCard(card);
    }

    /**
     * Удаляет карту по ID.
     * 
     * @param id уникальный идентификатор карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Transactional
    public void deleteById(UUID id) throws CardNotFoundException {
        cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Карта с id = " + id + " не найдена"));
        cardRepository.deleteById(id);
    }

    /**
     * Пополняет баланс карты.
     * 
     * <p>ВНИМАНИЕ: Это временная реализация для имитации пополнения
     * с номера телефона. В идеале следует создать системную карту
     * и перенести логику в TransactionService для сохранения
     * истории пополнений.</p>
     * 
     * @param cardId уникальный идентификатор карты
     * @param requestDto данные для пополнения баланса
     * @return карта с обновленным балансом в формате DTO
     * @throws CardNotFoundException если карта не найдена
     */
    @Transactional
    public CardResponseDto topUpCardBalance(UUID cardId, TopUpRequestDto requestDto, String username)  throws CardNotFoundException {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Карта с id = " + cardId + " не найдена"));

        if (!username.equals(card.getOwner().getUsername())){
            throw new TransactionTransferException("Пользователь может пополнять только свой баланс");
        }

        BigDecimal newBalance = card.getBalance().add(requestDto.getAmount());
        card.setBalance(newBalance);
        card = cardRepository.save(card);
        return cardMapper.toDtoFromCard(card);
    }
}

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
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.mapper.CardMapper;
import ru.ivanov.Bank.repository.CardRepository;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.util.CardNumberGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardNumberGenerator cardNumberGenerator;

    public Page<CardResponseDto> findAll(int pageNumber, int size){
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Card> cardPage = cardRepository.findAll(pageable);
        return cardPage.map(cardMapper::toDtoFromCard);
    }

    public CardResponseDto findById(UUID id) throws CardNotFoundException {
        Card card = cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Карта с id = " + id + " не найдена"));
        return cardMapper.toDtoFromCard(card);
    }

    public Page<CardResponseDto> findByOwnerId(UUID ownerId, int pageNumber, int size){
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + ownerId + " не найден"));
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Card> cards = cardRepository.findByOwner(owner, pageable);
        return cards.map(cardMapper::toDtoFromCard);
    }

    @Transactional
    public CardResponseDto save(CreateCardRequestDto requestDto) throws UserNotFoundException{
        Card card = cardMapper.toCardFromCreateRequest(requestDto);
        card.setCardNumber(cardNumberGenerator.generate());
        card.setOwner(userRepository.findById(requestDto.getOwnerId()).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + requestDto.getOwnerId() + " не найден")));
        card = cardRepository.save(card);
        return cardMapper.toDtoFromCard(card);
    }

    @Transactional
    public CardResponseDto blockCard(UUID id) throws CardNotFoundException {
        Card card = cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Карта с id = " + id + " не найдена"));
        card.setStatus(CardStatus.BLOCKED);
        card = cardRepository.save(card);
        return cardMapper.toDtoFromCard(card);
    }

    @Transactional
    public void deleteById(UUID id) throws CardNotFoundException {
        cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Карта с id = " + id + " не найдена"));
        cardRepository.deleteById(id);
    }

    /* TODO:
     чисто заглушка для пополнения баланса, имитирует перевод с номера телефона
     в идеале можно сделать "системную карту", UUID которой будем хранить, например, в пропертях/константе,
     создавать эту карту будем в нашем DataInitializer, данный метод перенесем в TransactionService,
     это позволит сохранять пополнения в истории, не ломая модель транзакций данного приложения
     */
    @Transactional
    public CardResponseDto topUpCardBalance(UUID cardId, TopUpRequestDto requestDto)  throws CardNotFoundException{
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Карта с id = " + cardId + " не найдена"));
        BigDecimal newBalance = card.getBalance().add(requestDto.getAmount());
        card.setBalance(newBalance);
        card = cardRepository.save(card);
        return cardMapper.toDtoFromCard(card);
    }
}

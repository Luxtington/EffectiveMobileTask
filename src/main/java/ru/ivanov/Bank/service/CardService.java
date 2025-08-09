package ru.ivanov.Bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.Bank.dto.CardResponseDto;
import ru.ivanov.Bank.dto.CreateCardRequestDto;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.CardStatus;
import ru.ivanov.Bank.exception.CardNotFoundException;
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.mapper.CardMapper;
import ru.ivanov.Bank.repository.CardRepository;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.util.CardNumberGenerator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;
    private final CardNumberGenerator cardNumberGenerator;

    public List<Card> findAll(){
        return cardRepository.findAll();
    }

    public Card findById(UUID id) throws CardNotFoundException {
        return cardRepository.findById(id).orElseThrow(() -> new CardNotFoundException("Карта с id = " + id + " не найдена"));
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
}

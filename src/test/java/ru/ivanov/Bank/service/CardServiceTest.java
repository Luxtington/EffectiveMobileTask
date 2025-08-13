package ru.ivanov.Bank.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ivanov.Bank.dto.CardResponseDto;
import ru.ivanov.Bank.dto.CreateCardRequestDto;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.entity.CardStatus;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.exception.CardNotFoundException;
import ru.ivanov.Bank.mapper.CardMapper;
import ru.ivanov.Bank.repository.CardRepository;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.util.CardNumberGenerator;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @InjectMocks
    private CardService cardService;

    @Test
    @DisplayName("Должен создать карту успешно")
    void shouldCreateCardSuccessfully() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        CreateCardRequestDto requestDto = new CreateCardRequestDto();
        requestDto.setOwnerId(userId);
        requestDto.setExpiryDate(LocalDate.of(2028, 12, 31));

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        Card card = new Card();
        card.setId(cardId);
        card.setCardNumber("1234 5678 9012 3456");
        card.setOwner(user);

        CardResponseDto expectedResponse = new CardResponseDto();
        expectedResponse.setId(cardId);
        expectedResponse.setCardNumber("**** **** **** 3456");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cardMapper.toCardFromCreateRequest(requestDto)).thenReturn(card);
        when(cardNumberGenerator.generate()).thenReturn("1234 5678 9012 3456");
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDtoFromCard(card)).thenReturn(expectedResponse);

        // When
        CardResponseDto result = cardService.save(requestDto);

        // Then
        assertThat(result.getId()).isEqualTo(cardId);
        assertThat(result.getCardNumber()).isEqualTo("**** **** **** 3456");
        verify(cardNumberGenerator).generate();
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Должен заблокировать карту")
    void shouldBlockCard() {
        // Given
        UUID cardId = UUID.randomUUID();

        Card card = new Card();
        card.setId(cardId);
        card.setStatus(CardStatus.ACTIVE);

        CardResponseDto expectedResponse = new CardResponseDto();
        expectedResponse.setId(cardId);
        expectedResponse.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDtoFromCard(card)).thenReturn(expectedResponse);

        // When
        CardResponseDto result = cardService.blockCard(cardId);

        // Then
        assertThat(result.getStatus()).isEqualTo(CardStatus.BLOCKED);
        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("Должен выбросить исключение при блокировке несуществующей карты")
    void shouldThrowExceptionWhenBlockingNonExistentCard() {
        // Given
        UUID cardId = UUID.randomUUID();
        when(cardRepository.findById(cardId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> cardService.blockCard(cardId))
                .isInstanceOf(CardNotFoundException.class);
    }
}
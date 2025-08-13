package ru.ivanov.Bank.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ivanov.Bank.dto.TransferRequestDto;
import ru.ivanov.Bank.entity.*;
import ru.ivanov.Bank.exception.TransactionTransferException;
import ru.ivanov.Bank.repository.CardRepository;
import ru.ivanov.Bank.repository.TransactionRepository;
import ru.ivanov.Bank.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Должен выполнить перевод между картами одного пользователя")
    void shouldTransferMoneyBetweenCardsOfSameUser() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID fromCardId = UUID.randomUUID();
        UUID toCardId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        Card fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setOwner(user);
        fromCard.setBalance(new BigDecimal("100.00"));
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpiryDate(LocalDate.of(2028, 12, 31));

        Card toCard = new Card();
        toCard.setId(toCardId);
        toCard.setOwner(user);
        toCard.setBalance(new BigDecimal("50.00"));
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpiryDate(LocalDate.of(2028, 12, 31));

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCardId);
        requestDto.setToCardId(toCardId);
        requestDto.setAmount(new BigDecimal("30.00"));

        lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        lenient().when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        lenient().when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));
        lenient().when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.transferMoneyBetweenCards(requestDto, "testuser");

        assertThat(result.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("30.00"));
        assertThat(fromCard.getBalance()).isEqualTo(new BigDecimal("70.00"));
        assertThat(toCard.getBalance()).isEqualTo(new BigDecimal("80.00"));

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
        verify(transactionRepository).save(result);
    }

    @Test
    @DisplayName("Должен выбросить исключение при переводе между картами разных пользователей")
    void shouldThrowExceptionWhenTransferringBetweenDifferentUsers() {
        UUID user1Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UUID fromCardId = UUID.randomUUID();
        UUID toCardId = UUID.randomUUID();

        User user1 = new User();
        user1.setId(user1Id);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(user2Id);
        user2.setUsername("user2");

        Card fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setOwner(user1);
        fromCard.setExpiryDate(LocalDate.of(2028, 12, 31));

        Card toCard = new Card();
        toCard.setId(toCardId);
        toCard.setOwner(user2);
        toCard.setExpiryDate(LocalDate.of(2028, 12, 31));

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCardId);
        requestDto.setToCardId(toCardId);
        requestDto.setAmount(new BigDecimal("30.00"));

        lenient().when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user1));
        lenient().when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        lenient().when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));

        assertThatThrownBy(() -> transactionService.transferMoneyBetweenCards(requestDto, "user1"))
                .isInstanceOf(TransactionTransferException.class)
                .hasMessageContaining("Перевод денежных средств может быть осуществлен только между картами одного пользователя");
    }

    @Test
    @DisplayName("Должен выбросить исключение при недостаточном балансе")
    void shouldThrowExceptionWhenInsufficientBalance() {
        UUID userId = UUID.randomUUID();
        UUID fromCardId = UUID.randomUUID();
        UUID toCardId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        Card fromCard = new Card();
        fromCard.setId(fromCardId);
        fromCard.setOwner(user);
        fromCard.setBalance(new BigDecimal("20.00"));
        fromCard.setStatus(CardStatus.ACTIVE);
        fromCard.setExpiryDate(LocalDate.of(2028, 12, 31));

        Card toCard = new Card();
        toCard.setId(toCardId);
        toCard.setOwner(user);
        toCard.setBalance(new BigDecimal("50.00"));
        toCard.setStatus(CardStatus.ACTIVE);
        toCard.setExpiryDate(LocalDate.of(2028, 12, 31));

        TransferRequestDto requestDto = new TransferRequestDto();
        requestDto.setFromCardId(fromCardId);
        requestDto.setToCardId(toCardId);
        requestDto.setAmount(new BigDecimal("30.00"));

        lenient().when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        lenient().when(cardRepository.findById(fromCardId)).thenReturn(Optional.of(fromCard));
        lenient().when(cardRepository.findById(toCardId)).thenReturn(Optional.of(toCard));

        assertThatThrownBy(() -> transactionService.transferMoneyBetweenCards(requestDto, "testuser"))
                .isInstanceOf(TransactionTransferException.class)
                .hasMessageContaining("Недостаточно средств");
    }
}
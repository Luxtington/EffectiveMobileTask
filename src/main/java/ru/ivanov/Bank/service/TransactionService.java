package ru.ivanov.Bank.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;

    public List<Transaction> findAll(){
        return transactionRepository.findAll();
    }

    public Transaction findById(UUID id) throws TransactionNotFoundException{
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException("Транзакция с id = " + id + " не найдена"));
    }

    @Transactional
    public Transaction transferMoneyBetweenCards(TransferRequestDto requestDto) throws CardNotFoundException {
        Card fromCard = cardRepository.findById(requestDto.getFromCardId()).orElseThrow(() -> new CardNotFoundException("Карта с id = " + requestDto.getFromCardId() + " не найдена"));
        Card toCard = cardRepository.findById(requestDto.getToCardId()).orElseThrow(() -> new CardNotFoundException("Карта с id = " + requestDto.getToCardId() + " не найдена"));

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

    @Transactional
    public void deleteById(UUID id){
        transactionRepository.deleteById(id);
    }
}

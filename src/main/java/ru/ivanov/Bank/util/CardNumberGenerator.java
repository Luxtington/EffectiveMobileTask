package ru.ivanov.Bank.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ivanov.Bank.entity.Card;
import ru.ivanov.Bank.exception.CardNumberGenerationException;
import ru.ivanov.Bank.repository.CardRepository;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CardNumberGenerator {
    private final CardRepository cardRepository;
    private final static int CARD_NUMBER_LENGTH = 16;
    private int MAX_ATTEMPTS = 50;
    private final static SecureRandom RANDOM = new SecureRandom();
    private final Set<String> allCardsNumbersCache = new HashSet<>();

    @PostConstruct
    private void loadCards(){
        List<Card> cards = cardRepository.findAll();
        for (Card card : cards){
            allCardsNumbersCache.add(card.getCardNumber());
        }
    }

    public String generate() throws CardNumberGenerationException {
        while (MAX_ATTEMPTS > 0){
            StringBuilder numberBuilder = new StringBuilder(CARD_NUMBER_LENGTH);
            for (int i = 0; i < CARD_NUMBER_LENGTH; i++){
                numberBuilder.append(RANDOM.nextInt(10));
            }
            String number = numberBuilder.toString();
            if (!allCardsNumbersCache.contains(number)){
                return formatCardNumber(number);
            }
            MAX_ATTEMPTS--;
        }
        throw new CardNumberGenerationException("Не удалось сгенерировать номер карты");
    }

    private String formatCardNumber(String number){
        return number.replaceAll("(.{4})", "$1 ").trim();
    }
}

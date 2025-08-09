package ru.ivanov.Bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ivanov.Bank.dto.CardResponseDto;
import ru.ivanov.Bank.dto.CreateCardRequestDto;
import ru.ivanov.Bank.entity.Card;

@Mapper(componentModel = "spring")
public interface CardMapper {
    @Mapping(target = "owner", ignore = true)
    Card toCardFromCreateRequest(CreateCardRequestDto requestDto);

    @Mapping(target = "cardNumber", expression = "java(maskCardNumber(card.getCardNumber()))")
    CardResponseDto toDtoFromCard(Card card);

    default String maskCardNumber(String number){
        String lastFourDigits = number.substring(number.length() - 4);
        return String.format("**** **** **** %s", lastFourDigits);
    }
}

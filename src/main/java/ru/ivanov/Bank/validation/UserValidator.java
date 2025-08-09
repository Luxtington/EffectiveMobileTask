package ru.ivanov.Bank.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.dto.UpdateUserRequestDto;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.exception.UserAlreadyExistsException;
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.repository.UserRepository;

import java.util.UUID;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateUserCreation(CreateUserRequestDto requestDto) throws  UserAlreadyExistsException {
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("Пользователь с таким логином уже существует");
        }
    }

    public void validateUserUpdate(UUID id, UpdateUserRequestDto requestDto) throws UserNotFoundException {
        User existsUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
        if (!existsUser.getUsername().equals(requestDto.getUsername()) && userRepository.findByUsername(requestDto.getUsername()).isPresent()){
            throw new UserAlreadyExistsException("Пользователь с таким логином уже существует");
        }
    }
}

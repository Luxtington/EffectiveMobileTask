package ru.ivanov.Bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.dto.UpdateUserRequestDto;
import ru.ivanov.Bank.dto.UserResponseDto;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.mapper.UserMapper;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.validation.UserValidator;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(UUID id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Transactional
    public UserResponseDto save(CreateUserRequestDto requestDto){
        userValidator.validateUserCreation(requestDto);

        User user = userMapper.toUserFromCreateRequest(requestDto, passwordEncoder);
        user = userRepository.save(user);
        return userMapper.toDtoFromUser(user);
    }

    @Transactional
    public UserResponseDto save(UpdateUserRequestDto requestDto, UUID id) throws UserNotFoundException {
        userValidator.validateUserUpdate(id, requestDto);

        User user = userMapper.toUserFromUpdateRequest(requestDto, passwordEncoder);
        User existsUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
        user.setId(id);
        user.setCreatedAt(existsUser.getCreatedAt());
        user.setRoles(existsUser.getRoles());
        user.setCards(existsUser.getCards());
        user = userRepository.save(user);

        return userMapper.toDtoFromUser(user);
    }

    @Transactional
    public void deleteById(UUID id) throws UserNotFoundException {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
        userRepository.deleteById(id);
    }
}

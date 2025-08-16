package ru.ivanov.Bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.dto.UpdateUserRequestDto;
import ru.ivanov.Bank.dto.UserResponseDto;
import ru.ivanov.Bank.entity.RoleType;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.exception.RoleNotFoundException;
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.mapper.UserMapper;
import ru.ivanov.Bank.repository.RoleRepository;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.validation.UserValidator;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Сервис для управления пользователями.
 * Предоставляет методы для работы с пользователями: поиск, создание,
 * обновление и удаление. Включает валидацию и маппинг данных.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final PasswordEncoder passwordEncoder;

    /**
     * Находит всех пользователей с пагинацией.
     * 
     * @param pageNumber номер страницы (начиная с 0)
     * @param size размер страницы
     * @return страница с пользователями
     */
    public Page<User> findAll(int pageNumber, int size){
        Pageable pageable = PageRequest.of(pageNumber, size);
        return userRepository.findAll(pageable);
    }

    /**
     * Находит пользователя по ID.
     * 
     * @param id уникальный идентификатор пользователя
     * @return найденный пользователь
     * @throws UserNotFoundException если пользователь не найден
     */
    public User findById(UUID id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
    }

    /**
     * Создает нового пользователя.
     * 
     * @param requestDto данные для создания пользователя
     * @return DTO созданного пользователя
     * @throws RoleNotFoundException если роль пользователя не найдена
     */
    @Transactional
    public UserResponseDto save(CreateUserRequestDto requestDto) throws RoleNotFoundException {
        userValidator.validateUserCreation(requestDto);

        User user = userMapper.toUserFromCreateRequest(requestDto, passwordEncoder);
        user.addRole(roleRepository.findByRoleType(RoleType.USER).orElseThrow(() -> new RoleNotFoundException("Роль пользователя не найдена")));
        user = userRepository.saveAndFlush(user);

        return userMapper.toDtoFromUser(user);
    }

    /**
     * Обновляет существующего пользователя.
     * 
     * @param requestDto данные для обновления пользователя
     * @param id уникальный идентификатор пользователя
     * @return DTO обновленного пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
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

    /**
     * Удаляет пользователя по ID.
     * 
     * @param id уникальный идентификатор пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Transactional
    public void deleteById(UUID id) throws UserNotFoundException {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + id + " не найден"));
        userRepository.deleteById(id);
    }
}

package ru.ivanov.Bank.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.dto.UserResponseDto;
import ru.ivanov.Bank.entity.Role;
import ru.ivanov.Bank.entity.RoleType;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.mapper.UserMapper;
import ru.ivanov.Bank.repository.RoleRepository;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.validation.UserValidator;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Должен создать пользователя успешно")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setPassword("password123");
        requestDto.setName("Test");
        requestDto.setSurname("User");

        UserResponseDto expectedResponse = new UserResponseDto();
        expectedResponse.setUsername("testuser");

        Role userRole = new Role(RoleType.USER);
        when(roleRepository.findByRoleType(RoleType.USER)).thenReturn(Optional.of(userRole));

        doReturn(new User()).when(userMapper).toUserFromCreateRequest(any(), any());
        doReturn(new User()).when(userRepository).saveAndFlush(any());  // ← Изменить здесь
        doReturn(expectedResponse).when(userMapper).toDtoFromUser(any());

        UserResponseDto result = userService.save(requestDto);

        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userValidator).validateUserCreation(requestDto);
        verify(userRepository).saveAndFlush(any());  // ← Изменить здесь
    }

    @Test
    @DisplayName("Должен выбросить исключение при создании пользователя с существующим логином")
    void shouldThrowExceptionWhenCreatingUserWithExistingUsername() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setUsername("existinguser");

        doThrow(new RuntimeException("Пользователь уже существует"))
                .when(userValidator).validateUserCreation(requestDto);

        assertThatThrownBy(() -> userService.save(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Пользователь уже существует");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Должен найти пользователя по ID")
    void shouldFindUserById() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.findById(userId);

        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Должен выбросить исключение при поиске несуществующего пользователя")
    void shouldThrowExceptionWhenFindingNonExistentUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }
}
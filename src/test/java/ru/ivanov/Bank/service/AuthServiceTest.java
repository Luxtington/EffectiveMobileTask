package ru.ivanov.Bank.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.ivanov.Bank.dto.AuthRequestDto;
import ru.ivanov.Bank.dto.AuthResponseDto;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.dto.UserResponseDto;
import ru.ivanov.Bank.entity.Role;
import ru.ivanov.Bank.entity.RoleType;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.mapper.UserMapper;
import ru.ivanov.Bank.repository.RoleRepository;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.security.JwtTokenProvider;
import ru.ivanov.Bank.validation.UserValidator;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Должен успешно залогинить пользователя")
    void shouldLoginUserSuccessfully() {
        // Given
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("testuser");
        requestDto.setPassword("password123");

        User user = new User();
        user.setUsername("testuser");
        user.setRoles(Set.of(new Role(RoleType.USER)));

        AuthResponseDto expectedResponse = new AuthResponseDto();
        expectedResponse.setToken("jwt-token");
        expectedResponse.setUsername("testuser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        AuthResponseDto result = authService.login(requestDto);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Должен зарегистрировать нового пользователя")
    void shouldRegisterNewUser() throws Throwable {
        // Given
        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setUsername("newuser");
        requestDto.setPassword("password123");
        requestDto.setName("New");
        requestDto.setSurname("User");

        Role userRole = new Role(RoleType.USER);
        User user = new User();
        user.setUsername("newuser");
        user.setRoles(Set.of(userRole));

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(roleRepository.findByRoleType(RoleType.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        AuthResponseDto result = authService.register(requestDto);

        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getUsername()).isEqualTo("newuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Должен выбросить исключение при регистрации существующего пользователя")
    void shouldThrowExceptionWhenRegisteringExistingUser() {
        CreateUserRequestDto requestDto = new CreateUserRequestDto();
        requestDto.setUsername("existinguser");

        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("уже зарегистрирован");
    }
}
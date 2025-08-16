package ru.ivanov.Bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ivanov.Bank.dto.AuthRequestDto;
import ru.ivanov.Bank.dto.AuthResponseDto;
import ru.ivanov.Bank.dto.CreateUserRequestDto;
import ru.ivanov.Bank.entity.RoleType;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.exception.RoleNotFoundException;
import ru.ivanov.Bank.exception.UserNotFoundException;
import ru.ivanov.Bank.repository.RoleRepository;
import ru.ivanov.Bank.repository.UserRepository;
import ru.ivanov.Bank.security.JwtTokenProvider;

import java.util.function.Supplier;

/**
 * Сервис для аутентификации и регистрации пользователей.
 * Предоставляет методы для входа в систему, регистрации новых пользователей,
 * проверки существования пользователей и изменения паролей.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Выполняет аутентификацию пользователя и возвращает JWT токен.
     * 
     * @param request данные для аутентификации (логин и пароль)
     * @return ответ с JWT токеном и информацией о пользователе
     * @throws UserNotFoundException если пользователь не найден
     */
    public AuthResponseDto login(AuthRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким логином не найден"));
        
        AuthResponseDto response = new AuthResponseDto();
        response.setToken(jwt);
        response.setUsername(user.getUsername());
        response.setRoles(user.getRoles());

        return response;
    }

    /**
     * Регистрирует нового пользователя в системе.
     * 
     * @param request данные для регистрации пользователя
     * @return ответ с JWT токеном и информацией о зарегистрированном пользователе
     * @throws RuntimeException если пользователь с таким логином уже существует
     * @throws RoleNotFoundException если роль пользователя не найдена в БД
     */
    public AuthResponseDto register(CreateUserRequestDto request) throws Throwable {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Пользователь с таким логином уже зарегистрирован");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setSurname(request.getSurname());
        user.setName(request.getName());
        user.setPatronymic(request.getPatronymic());
        user.setBirthdayYear(request.getBirthdayYear());
        user.addRole(roleRepository.findByRoleType(RoleType.USER).orElseThrow((Supplier<Throwable>) () -> new RoleNotFoundException("Роль пользователя не найдена в БД")));

        User savedUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        AuthResponseDto response = new AuthResponseDto();
        response.setToken(jwt);
        response.setUsername(savedUser.getUsername());
        response.setRoles(savedUser.getRoles());

        return response;
    }

    /**
     * Проверяет существование пользователя по логину.
     * 
     * @param username логин пользователя для проверки
     * @return true если пользователь существует, false в противном случае
     */
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * Изменяет пароль пользователя.
     * 
     * @param username логин пользователя
     * @param newPassword новый пароль (будет зашифрован)
     * @throws UserNotFoundException если пользователь не найден
     */
    public void changePassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким логином не существует"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

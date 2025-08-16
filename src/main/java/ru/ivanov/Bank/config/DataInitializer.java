package ru.ivanov.Bank.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.ivanov.Bank.entity.Role;
import ru.ivanov.Bank.entity.RoleType;
import ru.ivanov.Bank.entity.User;
import ru.ivanov.Bank.repository.RoleRepository;
import ru.ivanov.Bank.repository.UserRepository;

import java.util.Set;

/**
 * Компонент для инициализации начальных данных в базе данных.
 * Автоматически создает роли (ADMIN, USER) и первого администратора
 * при запуске приложения, если они отсутствуют в базе данных.
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Заполняет базу данных начальными данными.
     * Выполняется автоматически после создания всех бинов Spring.
     * Создает роли ADMIN и USER, а также первого администратора системы.
     */
    @PostConstruct
    public void fillDb(){

        Role roleAdmin = roleRepository.findByRoleType(RoleType.ADMIN).orElse(null);
        if (roleAdmin == null){
            roleAdmin = new Role(RoleType.ADMIN);
            roleRepository.save(roleAdmin);
            log.info("Role of admin was inserted into db");
        }

        Role roleUser = roleRepository.findByRoleType(RoleType.USER).orElse(null);
        if (roleUser == null){
            roleUser = new Role(RoleType.USER);
            roleRepository.save(roleUser);
            log.info("Role of user was inserted into db");
        }
        if (userRepository.findAllByRoleType(RoleType.ADMIN.toString()).isEmpty()){
            User admin1 = new User("admin1", "admin1", "admin1", 1999, "admin1", passwordEncoder.encode("admin1"));
            admin1.setRoles(Set.of(roleAdmin));
            userRepository.save(admin1);
            log.info("First admin was saved in db");
        }
    }
}

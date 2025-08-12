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

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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

package ru.ivanov.Bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ivanov.Bank.entity.RoleType;
import ru.ivanov.Bank.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM \"user\" u WHERE u.id IN (SELECT user_id from user_role JOIN role ON user_role.role_id = role.id WHERE role.name = :roleName)",
           nativeQuery = true)
    List<User> findAllByRoleType(@Param("roleName") String roleName);
}

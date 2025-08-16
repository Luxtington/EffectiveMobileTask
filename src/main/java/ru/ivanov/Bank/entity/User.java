package ru.ivanov.Bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.ivanov.Bank.validation.ValidBirthdayYear;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Сущность пользователя банковской системы.
 * Представляет пользователя с персональными данными, учетными данными
 * и связями с ролями и картами. Использует JPA для маппинга на таблицу "user".
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Entity
@ToString()
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Setter
@Getter
@NoArgsConstructor
@Table(name = "\"user\"")
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Фамилия пользователя.
     * Должна быть не пустой и содержать от 1 до 30 символов.
     */
    @NotEmpty(message = "Фамилия не может быть пустой")
    @NotNull
    @Size(min = 1, max = 30, message = "Размер фамилии должен быть в диапазоне от 1 от до 30 символов")
    private String surname;

    /**
     * Имя пользователя.
     * Должно быть не пустым и содержать от 2 до 15 символов.
     */
    @NotEmpty(message = "Имя не может быть пустым")
    @NotNull
    @Size(min = 2, max = 15, message = "Размер имени должен быть в диапазоне от 2 от до 15 символов")
    private String name;

    /**
     * Отчество пользователя.
     * Может быть пустым, но если указано, должно содержать от 5 до 20 символов.
     */
    @Size(min = 5, max = 20, message = "Размер отчества должен быть в диапазоне от 5 от до 20 символов")
    private String patronymic;

    /**
     * Год рождения пользователя.
     * Должен быть в диапазоне от 1910 до 2025.
     */
    @ValidBirthdayYear
    private int birthdayYear;

    /**
     * Логин пользователя.
     * Должен быть уникальным, не пустым и содержать от 1 до 30 символов.
     */
    @NotEmpty(message = "Логин не может быть пустым")
    @NotNull
    @Size(min = 1, max = 30, message = "Размер логина должен быть в диапазоне от 1 от до 30 символов")
    private String username;

    /**
     * Пароль пользователя.
     * Должен быть не пустым и содержать от 1 до 100 символов.
     * Хранится в виде TEXT для поддержки длинных хешей.
     */
    @NotEmpty(message = "Пароль не может быть пустым")
    @NotNull
    @Size(min = 1, max = 100, message = "Размер пароля должен быть в диапазоне от 1 от до 30 символов")
    @Column(columnDefinition = "TEXT")
    private String password;

    /**
     * Дата и время создания пользователя.
     * Автоматически устанавливается при создании записи.
     */
    @CreationTimestamp()
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * Роли пользователя.
     * Связь многие-ко-многим с таблицей ролей.
     */
    @NotNull
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns =  @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    /**
     * Карты пользователя.
     * Связь один-ко-многим с таблицей карт.
     * Исключена из JSON сериализации для безопасности.
     */
    @JsonIgnore
    @NotNull
    @OneToMany(mappedBy = "owner")
    private List<Card> cards = new ArrayList<>();

    /**
     * Конструктор с параметрами для создания пользователя.
     * 
     * @param surname фамилия пользователя
     * @param name имя пользователя
     * @param patronymic отчество пользователя
     * @param birthdayYear год рождения пользователя
     * @param username логин пользователя
     * @param password пароль пользователя
     */
    public User(String surname, String name, String patronymic, int birthdayYear, String username, String password) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.birthdayYear = birthdayYear;
        this.username = username;
        this.password = password;
    }

    /**
     * Добавляет роль пользователю.
     * 
     * @param role роль для добавления
     */
    public void addRole(@NotNull Role role){
        roles.add(role);
    }

    /**
     * Добавляет карту пользователю и устанавливает связь.
     * 
     * @param card карта для добавления
     */
    public void addCard(@NotNull Card card){
        cards.add(card);
        card.setOwner(this);
    }
}

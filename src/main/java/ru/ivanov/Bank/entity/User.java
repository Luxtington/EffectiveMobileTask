package ru.ivanov.Bank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import ru.ivanov.Bank.validation.ValidBirthdayYear;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@ToString(exclude = "createdAt")
@Setter
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotEmpty(message = "Фамилия не может быть пустой")
    @NotNull
    @Size(min = 1, max = 30, message = "Размер фамилии должен быть в диапазоне от 1 от до 30 символов")
    private String surname;

    @NotEmpty(message = "Имя не может быть пустым")
    @NotNull
    @Size(min = 2, max = 15, message = "Размер имени должен быть в диапазоне от 2 от до 15 символов")
    private String name;

    @Size(min = 0, max = 20, message = "Размер отчества должен быть в диапазоне от 5 от до 20 символов")
    private String patronymic;

    @ValidBirthdayYear
    private int birthdayYear;

    @NotEmpty(message = "Логин не может быть пустым")
    @NotNull
    @Size(min = 1, max = 30, message = "Размер логина должен быть в диапазоне от 1 от до 30 символов")
    private String username;

    @NotEmpty(message = "Пароль не может быть пустым")
    @NotNull
    @Size(min = 1, max = 100, message = "Размер пароля должен быть в диапазоне от 1 от до 30 символов")
    private String password;

    @CreationTimestamp()
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns =  @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @NotNull
    @OneToMany(mappedBy = "owner")
    private List<Card> cards = new ArrayList<>();

    public void addCard(@NotNull Card card){
        cards.add(card);
        card.setOwner(this);
    }
}

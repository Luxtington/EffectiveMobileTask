package ru.ivanov.Bank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Сущность роли пользователя.
 * Представляет роль пользователя в системе с уникальным типом роли.
 * Использует JPA для маппинга на таблицу "role".
 * 
 * @author Ilia Ivanov
 * @version 1.0
 * @since 2025
 */
@Entity
@EqualsAndHashCode(of = "roleType")
@ToString
@Setter
@Getter
@NoArgsConstructor
public class Role {
    /**
     * Уникальный идентификатор роли.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Тип роли.
     * Должен быть уникальным и не может быть null.
     */
    @Column(name = "name", unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    /**
     * Конструктор с параметром для создания роли.
     * 
     * @param roleType тип роли
     */
    public Role(RoleType roleType) {
        this.roleType = roleType;
    }
}

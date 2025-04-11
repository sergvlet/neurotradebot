package com.chicu.neurotradebot.telegramm.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Идентификатор пользователя

    @Column(nullable = false, unique = true)
    private Long chatId;  // ID чата в Telegram, уникальный идентификатор пользователя в Telegram

    @Column(nullable = true)
    private String testApiKey;  // Тестовый API ключ (если используется)

    @Column(nullable = true)
    private String realApiKey;  // Реальный API ключ (если используется)

    @Column(nullable = true)
    private String testApiSecret;  // Секретный ключ для тестового аккаунта

    @Column(nullable = true)
    private String realApiSecret;  // Секретный ключ для реального аккаунта

    @Column(nullable = true)
    private Boolean isDemoMode;  // Флаг, показывающий, активирован ли демонстрационный режим

    // Прочие параметры пользователя, которые могут быть полезны
    @Column(nullable = true)
    private String username;  // Имя пользователя Telegram

    @Column(nullable = true)
    private String firstName;  // Имя пользователя

    @Column(nullable = true)
    private String lastName;  // Фамилия пользователя

}

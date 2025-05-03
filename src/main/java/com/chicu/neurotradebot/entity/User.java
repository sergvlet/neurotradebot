// src/main/java/com/chicu/neurotradebot/entity/User.java
package com.chicu.neurotradebot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "telegram_user_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;

    @Column(name = "telegram_user_id", nullable = false, unique = true)
    private Long   telegramUserId;

    @Column(length = 100, nullable = true)
    private String username;

    @Column(name = "first_name", length = 100, nullable = true)  // теперь может быть NULL
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = true)
    private String lastName;

    @Column(name = "language_code", length = 10, nullable = true)
    private String languageCode;

    @Column(name = "phone_number", length = 32, nullable = true)
    private String phoneNumber;
}

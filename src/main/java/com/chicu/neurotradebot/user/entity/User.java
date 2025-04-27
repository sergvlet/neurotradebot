package com.chicu.neurotradebot.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    private Long id; // Telegram ID как Primary Key

    private String firstName;
    private String lastName;
    private String username;
    private String languageCode;

    private Boolean canJoinGroups;
    private Boolean canReadAllGroupMessages;
    private Boolean supportsInlineQueries;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TradingMode tradingMode = TradingMode.MANUAL; // ✅ новое поле

    public enum TradingMode {
        MANUAL,
        AI
    }
}

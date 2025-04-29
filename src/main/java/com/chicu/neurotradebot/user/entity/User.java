package com.chicu.neurotradebot.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    private Long id; // Telegram ID (Primary Key)

    private String username;
    private String firstName;
    private String lastName;

    private LocalDateTime createdAt;

    private LocalDateTime subscriptionStartAt;
    private LocalDateTime subscriptionEndAt;

    private Boolean subscriptionActive;
    private Boolean trialUsed;

    // 📌 Добавляем связь с таблицей user_trading_settings
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserTradingSettings tradingSettings;

    public boolean isSubscriptionActive() {
        return Boolean.TRUE.equals(subscriptionActive);
    }

    public boolean isTrialUsed() {
        return Boolean.TRUE.equals(trialUsed);
    }
}

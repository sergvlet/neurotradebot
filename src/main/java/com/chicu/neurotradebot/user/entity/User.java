package com.chicu.neurotradebot.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private Long id; // Telegram ID

    private String username;
    private String firstName;
    private String lastName;
    private Boolean registered;

    private LocalDateTime createdAt;

    private LocalDateTime subscriptionStartAt; // дата начала подписки
    private LocalDateTime subscriptionEndAt;   // дата окончания подписки

    private Boolean subscriptionActive;
    private Boolean trialUsed;

    public boolean isSubscriptionActive() {
        return Boolean.TRUE.equals(subscriptionActive);
    }

    public boolean isTrialUsed() {
        return Boolean.TRUE.equals(trialUsed);
    }
    public boolean isRegistered() {
        return subscriptionStartAt != null;
    }

}

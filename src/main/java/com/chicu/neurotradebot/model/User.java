package com.chicu.neurotradebot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    private Long id; // Telegram ID

    private String username;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime subscriptionStartAt;
    private LocalDateTime subscriptionEndAt;

    @Column(name = "subscription_active")
    private Boolean subscriptionActive;
    private Boolean trialUsed;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserTradingSettings tradingSettings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExchangeCredential> exchangeCredentials = new ArrayList<>();

    public boolean isSubscriptionActive() {
        return Boolean.TRUE.equals(subscriptionActive);
    }
    public boolean isTrialUsed() {
        return Boolean.TRUE.equals(trialUsed);
    }

    public User(Long id) {
        this.id = id;
    }
}

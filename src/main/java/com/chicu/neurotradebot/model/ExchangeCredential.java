package com.chicu.neurotradebot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_credentials")
@Getter
@Setter
@NoArgsConstructor
public class ExchangeCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Привязка к пользователю
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Название биржи (Binance, Bybit и т.д.)
    private String exchange;

    /**
     * Флаг, указывающий, что эта конкретная пара ключей
     * предназначена для TESTNET.
     * Не означает, что пользователь сейчас в тестовой сети.
     */
    private Boolean useTestnet;

    // API-ключи для реальной торговли
    private String realApiKey;
    private String realSecretKey;

    // API-ключи для тестовой торговли
    private String testApiKey;
    private String testSecretKey;

    private LocalDateTime createdAt;
}

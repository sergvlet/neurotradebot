package com.chicu.neurotradebot.user.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "exchange_settings")
public class ExchangeSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String exchange; // Например, "BINANCE", "BYBIT", "KUCOIN"

    private Boolean useTestnet = true; // Для каждой биржи отдельно

    private Boolean isActive = true; // Активна ли биржа для пользователя

    private LocalDateTime createdAt; // ➔ Вот это новое поле

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

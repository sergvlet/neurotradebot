// src/main/java/com/chicu/neurotradebot/entity/AiTradeSettings.java
package com.chicu.neurotradebot.entity;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.enums.TradeMode;
import com.chicu.neurotradebot.trade.strategy.entity.RiskConfig;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Основная сущность настроек AI-режима пользователя.
 */
@Entity
@Table(name = "ai_trade_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiTradeSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Владелец настроек (пользователь Telegram) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    /** Биржа: например "binance", "ftx" и т.д. */
    @Column(length = 32)
    @Builder.Default
    private String exchange = "";

    /** true — TESTNET, false — REAL */
    @Column(name = "test_mode", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean testMode = false;

    /** Шаг настройки API-ключей */
    @Enumerated(EnumType.STRING)
    @Column(name = "api_setup_step",
            nullable = false,
            columnDefinition = "varchar(32) default 'NONE'")
    @Builder.Default
    private ApiSetupStep apiSetupStep = ApiSetupStep.NONE;

    /** Когда запись создана */
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    /** Когда запись в последний раз обновлялась */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Сохранённые API-учётные данные для этой записи */
    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApiCredentials> credentials = new ArrayList<>();

    /** ID последнего подсказочного сообщения бота при вводе ключей */
    @Column(name = "api_setup_prompt_msg_id")
    private Integer apiSetupPromptMsgId;

    // ========== Параметры AI-режима ==========

    /**
     * true — автоматическая торговля включена; false — отключена.
     * Используйте этот флаг в сервисах и хендлерах вместо ранее предложенного «tradingEnabled».
     */
    @Column(name = "enabled", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean enabled = false;

    /** Режим торговли (SPOT, MARGIN, FUTURES_USDT, FUTURES_COIN) */
    @Enumerated(EnumType.STRING)
    @Column(name = "trade_mode",
            length = 32,
            nullable = false,
            columnDefinition = "varchar(32) default 'SPOT'")
    @Builder.Default
    private TradeMode tradeMode = TradeMode.SPOT;

    /** Список валютных пар */
    @ElementCollection
    @CollectionTable(name = "ai_trade_pairs",
            joinColumns = @JoinColumn(name = "settings_id"))
    @Column(name = "pair", length = 16)
    @Builder.Default
    private List<String> pairs = new ArrayList<>();

    /** Выбранная стратегия (RSI_MACD, EMA_CROSSOVER и т.д.) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "varchar(32) default 'RSI_MACD'")
    @Builder.Default
    private StrategyType strategy = StrategyType.RSI_MACD;

    /** Интервал сканирования в миллисекундах */
    @Column(name = "scan_interval",
            nullable = false,
            columnDefinition = "numeric(21,0) default 60000")
    @Builder.Default
    private Duration scanInterval = Duration.ofMinutes(1);

    /** Встроенный конфиг параметров RSI+MACD */
    @Embedded
    @Builder.Default
    private RsiMacdConfig rsiMacdConfig = new RsiMacdConfig();

    /** Встроенный конфиг управления рисками */
    @Embedded
    @Builder.Default
    private RiskConfig riskConfig = new RiskConfig();

    // ============================================

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        apiSetupStep = ApiSetupStep.NONE;
        apiSetupPromptMsgId = null;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}

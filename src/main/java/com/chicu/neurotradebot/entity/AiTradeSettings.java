// src/main/java/com/chicu/neurotradebot/entity/AiTradeSettings.java
package com.chicu.neurotradebot.entity;

import com.chicu.neurotradebot.enums.ApiSetupStep;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.enums.TradeMode;
import com.chicu.neurotradebot.trade.strategy.entity.RiskConfig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

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
    @Column(name = "test_mode", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private boolean testMode = false;

    /** Шаг настройки API-ключей */
    @Enumerated(EnumType.STRING)
    @Column(name = "api_setup_step", nullable = false, length = 32)
    @ColumnDefault("'NONE'")
    @Builder.Default
    private ApiSetupStep apiSetupStep = ApiSetupStep.NONE;

    /** ID подсказочного сообщения при вводе ключей */
    @Column(name = "api_setup_prompt_msg_id")
    private Integer apiSetupPromptMsgId;

    /** Когда запись создана */
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    /** Когда запись обновлялась */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** API-учётные данные */
    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApiCredentials> credentials = new ArrayList<>();

    // ========== Параметры AI-режима ==========

    /** Включена ли автоматическая торговля */
    @Column(name = "enabled", nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private boolean enabled = false;

    /** Режим торговли */
    @Enumerated(EnumType.STRING)
    @Column(name = "trade_mode", nullable = false, length = 32)
    @ColumnDefault("'SPOT'")
    @Builder.Default
    private TradeMode tradeMode = TradeMode.SPOT;

    /** Валютные пары */
    @ElementCollection
    @CollectionTable(
      name = "ai_trade_pairs",
      joinColumns = @JoinColumn(name = "settings_id")
    )
    @Column(name = "pair", length = 16)
    @Builder.Default
    private List<String> pairs = new ArrayList<>();

    /** Выбранные стратегии */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
      name = "ai_trade_strategies",
      joinColumns = @JoinColumn(name = "settings_id")
    )
    @Column(name = "strategy", length = 32)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<StrategyType> strategies = new HashSet<>();

    /** Интервал сканирования */
    @Column(name = "scan_interval", nullable = false)
    @ColumnDefault("60000")
    @Builder.Default
    private Duration scanInterval = Duration.ofMinutes(1);

    /** Общий конфиг риск-менеджмента */
    @Embedded
    @Builder.Default
    private RiskConfig riskConfig = new RiskConfig();

    // ========== Отдельные таблицы под стратегии ==========

    @OneToOne(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private RsiConfig rsiConfig;

    @OneToOne(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MacdConfig macdConfig;

    @OneToOne(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EmaCrossoverConfig emaCrossoverConfig;

    @OneToOne(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BollingerConfig bollingerConfig;

    @OneToOne(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DcaConfig dcaConfig;

    @OneToOne(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ScalpingConfig scalpingConfig;

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

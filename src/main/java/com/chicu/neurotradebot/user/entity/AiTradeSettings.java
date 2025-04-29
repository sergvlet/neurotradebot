package com.chicu.neurotradebot.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Настройки AI-торговли пользователя.
 */
@Entity
@Getter
@Setter
@Table(name = "ai_trade_settings")
public class AiTradeSettings {

    /**
     * ID пользователя (user_id), совпадает с ID UserTradingSettings.
     */
    @Id
    private Long id;

    /**
     * Связь с общими настройками торговли пользователя.
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserTradingSettings userTradingSettings;

    /**
     * Выбранная AI-стратегия.
     * Например: "Сбалансированная", "Консервативная", "Агрессивная".
     */
    @Column(name = "strategy")
    private String strategy;

    /**
     * Уровень риска для AI-стратегии.
     * Например: "Низкий", "Средний", "Высокий".
     */
    @Column(name = "risk")
    private String risk;

    /**
     * Тип торговли.
     * Например: "Спотовая торговля".
     */
    @Column(name = "trading_type")
    private String tradingType;

    /**
     * Автоматический запуск торговли после настроек.
     * true — включён, false — отключён.
     */
    @Column(name = "autostart")
    private Boolean autostart;

    /**
     * Отправлять ли уведомления в Telegram о действиях AI.
     * true — включены, false — отключены.
     */
    @Column(name = "notifications")
    private Boolean notifications;

    /**
     * Режим выбора валютной пары: MANUAL (ручной), LIST (из списка), AUTO (автоматический выбор).
     */
    @Column(name = "pair_mode")
    private String pairMode;

    /**
     * Выбранная вручную валютная пара, если режим MANUAL.
     * Пример: "BTC/USDT"
     */
    @Column(name = "manual_pair")
    private String manualPair;

    /**
     * Разрешённые валютные пары (CSV-список), если режим LIST.
     * Пример: "BTC/USDT,ETH/USDT,BNB/USDT"
     */
    @Column(name = "allowed_pairs", length = 1000)
    private String allowedPairs;


}

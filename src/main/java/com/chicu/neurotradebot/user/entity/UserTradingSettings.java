package com.chicu.neurotradebot.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Общие торговые настройки пользователя.
 * Связаны один-к-одному с сущностью User через user_id.
 * Хранят выбранную биржу, режим торговли и сеть (тест/реал).
 */
@Entity
@Table(name = "user_trading_settings")
@Getter
@Setter
@NoArgsConstructor
public class UserTradingSettings {

    /**
     * ID пользователя (Telegram ID), используется как Primary Key.
     */
    @Id
    private Long userId;

    /**
     * Связь с основной сущностью User.
     * Загрузка и сохранение синхронизированы через @MapsId.
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Выбранная биржа для торговли.
     * Примеры: Binance, Bybit и т.д.
     */
    @Column(name = "exchange")
    private String exchange;

    /**
     * Использовать ли тестовую сеть.
     * true — тестовая сеть, false — реальная сеть.
     */
    @Column(name = "use_testnet")
    private Boolean useTestnet;

    /**
     * Выбранный режим торговли.
     * Значения: "MANUAL" (ручная торговля) или "AI" (автоматическая торговля).
     */
    @Column(name = "trading_mode")
    private String tradingMode;

    /**
     * Связь с AI-торговыми настройками пользователя.
     * Каскадное сохранение и удаление настроек.
     */
    @OneToOne(mappedBy = "userTradingSettings", cascade = CascadeType.ALL, orphanRemoval = true)
    private AiTradeSettings aiTradeSettings;
}

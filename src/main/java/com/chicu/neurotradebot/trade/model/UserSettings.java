package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.enums.TradeMode;
import com.chicu.neurotradebot.trade.enums.TradeType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class UserSettings {

    @Id
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_name")
    private Exchange exchange = Exchange.BINANCE;

    private String exchangeSymbol = "BTCUSDT";

    private String timeframe = "1h";

    private String tradeLimit;

    @Enumerated(EnumType.STRING)
    private AvailableStrategy selectedManualStrategy; // Стратегия для ручной торговли

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_strategies", joinColumns = @JoinColumn(name = "chat_id"))
    @Column(name = "strategy")
    private Set<AvailableStrategy> strategies = new HashSet<>();  // Множество стратегий

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_mode")
    private TradeMode tradeMode = TradeMode.DEMO; // По умолчанию DEMO

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type")
    private TradeType tradeType = TradeType.AI;  // По умолчанию AI торговля

    @OneToMany(mappedBy = "userSettings", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<StrategyConfigEntity> strategyConfigs; // Связь с конфигурациями стратегий

    public String getStrategyText() {
        if (tradeType == TradeType.AI) {
            return strategies.isEmpty() ? "Не выбрано ❌" :
                    strategies.stream()
                            .map(s -> "✅ " + s.getTitle())
                            .sorted()
                            .reduce((a, b) -> a + "\n" + b)
                            .orElse("Не выбрано ❌");
        } else if (tradeType == TradeType.MANUAL && selectedManualStrategy != null) {
            return "✅ " + selectedManualStrategy.getTitle();
        }
        return "Не выбрано ❌";
    }
}

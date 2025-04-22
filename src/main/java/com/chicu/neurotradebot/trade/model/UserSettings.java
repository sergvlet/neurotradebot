package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.enums.TradeMode;
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
    private Exchange exchange = Exchange.BINANCE; // ✅ теперь enum с дефолтным значением

    private String exchangeSymbol = "BTCUSDT"; // ⚠️ по умолчанию

    private String timeframe = "1h"; // ⚠️ по умолчанию

    private String tradeLimit; // сумма сделки в USDT (как строка)

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_strategies",
            joinColumns = @JoinColumn(name = "chat_id")
    )
    @Column(name = "strategy")
    private Set<AvailableStrategy> strategies = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_mode")
    private TradeMode tradeMode = TradeMode.DEMO;
}

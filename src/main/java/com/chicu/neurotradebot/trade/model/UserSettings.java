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
    private AvailableStrategy selectedManualStrategy; // ✅ для ручной торговли

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_strategies", joinColumns = @JoinColumn(name = "chat_id"))
    @Column(name = "strategy")
    private Set<AvailableStrategy> strategies = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_mode")
    private TradeMode tradeMode = TradeMode.DEMO;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type")
    private TradeType tradeType = TradeType.AI;
}

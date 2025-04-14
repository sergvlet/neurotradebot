package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class UserSettings {

    @Id
    private Long chatId;

    private String exchange; // Пример: "BINANCE", "BYBIT"

    private boolean demoMode; // true = демо, false = реальный режим

    private String tradeLimit; // сумма сделки в USDT (как строка)

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_strategies",
            joinColumns = @JoinColumn(name = "chat_id")
    )
    @Column(name = "strategy")
    private Set<AvailableStrategy> strategies = new HashSet<>();
}

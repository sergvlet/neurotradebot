// src/main/java/com/chicu/neurotradebot/trade/risk/RiskManager.java
package com.chicu.neurotradebot.trade.risk;

import com.chicu.neurotradebot.entity.RiskConfig;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RiskManager {

    /**
     * Рассчитывает параметры ордера:
     *  - объём (макс.% от баланса)
     *  - stop-loss и take-profit цены
     *
     * @param config     параметры риска из AiTradeSettings
     * @param freeBalance свободный баланс котируемой валюты
     * @param entryPrice цена входа (последняя цена)
     */
    public RiskResult calculate(RiskConfig config,
                                BigDecimal freeBalance,
                                BigDecimal entryPrice) {
        BigDecimal pct   = config.getMaxPercentPerTrade()
                                 .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        BigDecimal amount = freeBalance.multiply(pct)
                                       .divide(entryPrice, 8, RoundingMode.DOWN);

        BigDecimal slPrice = entryPrice.multiply(
            BigDecimal.ONE.subtract(
                config.getStopLossPercent()
                      .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
            )
        ).setScale(8, RoundingMode.HALF_UP);

        BigDecimal tpPrice = entryPrice.multiply(
            BigDecimal.ONE.add(
                config.getTakeProfitPercent()
                      .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
            )
        ).setScale(8, RoundingMode.HALF_UP);

        return new RiskResult(amount, slPrice, tpPrice);
    }
}

// src/main/java/com/chicu/neurotradebot/trade/risk/RiskManager.java
package com.chicu.neurotradebot.trade.risk;

import com.chicu.neurotradebot.trade.strategy.entity.RiskConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Slf4j
public class RiskManager {

    /**
     * Рассчитывает параметры ордера:
     *  - объём (макс.% от баланса)
     *  - stop-loss и take-profit цены
     *
     * @param config       параметры риска из AiTradeSettings
     * @param freeBalance  свободный баланс котируемой валюты
     * @param entryPrice   цена входа (последняя цена)
     */
    public RiskResult calculate(RiskConfig config,
                                BigDecimal freeBalance,
                                BigDecimal entryPrice) {
        // Проверяем входные параметры
        if (config == null) {
            log.warn("RiskConfig is null → выдаём нулевой результат");
            return RiskResult.empty();
        }
        if (freeBalance == null || freeBalance.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("freeBalance пустой или <= 0 ({}). Нечего рисковать.", freeBalance);
            return RiskResult.empty();
        }
        if (entryPrice == null || entryPrice.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("entryPrice пустой или <= 0 ({}). Нечего рассчитывать.", entryPrice);
            return RiskResult.empty();
        }

        BigDecimal pct = config.getMaxPercentPerTrade();
        if (pct == null || pct.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("MaxPercentPerTrade пустой или <= 0 ({}).", pct);
            return RiskResult.empty();
        }

        // 1) Доля от баланса, которую рискуем
        BigDecimal pctFraction = pct
                .divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);

        // 2) Рассчитываем объём лота: amount = freeBalance * pctFraction / entryPrice
        BigDecimal amount = freeBalance
                .multiply(pctFraction)
                .divide(entryPrice, 8, RoundingMode.DOWN);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Calculated amount <= 0 (freeBalance={} pctFraction={} entryPrice={})",
                    freeBalance, pctFraction, entryPrice);
            return RiskResult.empty();
        }

        // 3) Расчёт уровней SL и TP
        BigDecimal slPct = config.getStopLossPercent();
        BigDecimal tpPct = config.getTakeProfitPercent();
        if (slPct == null) slPct = BigDecimal.ZERO;
        if (tpPct == null) tpPct = BigDecimal.ZERO;

        BigDecimal slPrice = entryPrice
                .multiply(
                        BigDecimal.ONE.subtract(
                                slPct.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
                        )
                )
                .setScale(8, RoundingMode.HALF_UP);

        BigDecimal tpPrice = entryPrice
                .multiply(
                        BigDecimal.ONE.add(
                                tpPct.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
                        )
                )
                .setScale(8, RoundingMode.HALF_UP);

        return new RiskResult(amount, slPrice, tpPrice);
    }
}

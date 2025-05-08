// src/main/java/com/chicu/neurotradebot/trade/risk/RiskManager.java
package com.chicu.neurotradebot.trade.risk;

import com.chicu.neurotradebot.trade.strategy.entity.RiskConfig;
import com.chicu.neurotradebot.trade.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
@Slf4j
public class RiskManager {

    private final AccountService accountService;

    /**
     * Возвращает свободный баланс:
     *  - при BUY — котируемая валюта (USDT, BUSD и т. д.)
     *  - при SELL — базовая валюта (BTC, ETH и т. п.)
     *
     * @param userId  Telegram-chatId пользователя
     * @param symbol  пара, например "BTCUSDT"
     * @param isBuy   true для BUY, false для SELL
     */
    public BigDecimal getFreeBalance(Long userId,
                                     String symbol,
                                     boolean isBuy) {
        String quote = symbol.endsWith("USDT") || symbol.endsWith("BUSD")
                ? symbol.substring(symbol.length() - 4)
                : symbol.substring(symbol.length() - 3);
        String base = symbol.substring(0, symbol.length() - quote.length());

        BigDecimal free = isBuy
                ? accountService.getFreeBalance(userId, quote)
                : accountService.getFreeBalance(userId, base);

        log.debug("getFreeBalance(userId={}, symbol={}, isBuy={}) → {}", userId, symbol, isBuy, free);
        return free != null ? free : BigDecimal.ZERO;
    }

    /**
     * Рассчёт объёма, SL и TP на основании freeBalance и цены входа.
     */
    public RiskResult calculate(RiskConfig config,
                                BigDecimal freeBalance,
                                BigDecimal entryPrice) {
        if (config == null) {
            log.warn("RiskConfig is null → empty");
            return RiskResult.empty();
        }
        if (freeBalance == null || freeBalance.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("freeBalance ≤0 ({}) → empty", freeBalance);
            return RiskResult.empty();
        }
        if (entryPrice == null || entryPrice.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("entryPrice ≤0 ({}) → empty", entryPrice);
            return RiskResult.empty();
        }

        BigDecimal pct = config.getMaxPercentPerTrade();
        if (pct == null || pct.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("MaxPercentPerTrade ≤0 ({}) → empty", pct);
            return RiskResult.empty();
        }

        BigDecimal fraction = pct.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        BigDecimal amount = freeBalance.multiply(fraction)
                .divide(entryPrice, 8, RoundingMode.DOWN);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Calculated amount ≤0 → empty");
            return RiskResult.empty();
        }

        BigDecimal slPct = config.getStopLossPercent() != null
                ? config.getStopLossPercent() : BigDecimal.ZERO;
        BigDecimal tpPct = config.getTakeProfitPercent() != null
                ? config.getTakeProfitPercent() : BigDecimal.ZERO;

        BigDecimal slPrice = entryPrice.multiply(
                BigDecimal.ONE.subtract(slPct.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP))
        ).setScale(8, RoundingMode.HALF_UP);

        BigDecimal tpPrice = entryPrice.multiply(
                BigDecimal.ONE.add(tpPct.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP))
        ).setScale(8, RoundingMode.HALF_UP);

        return new RiskResult(amount, slPrice, tpPrice);
    }
}

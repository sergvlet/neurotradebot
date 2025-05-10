package com.chicu.neurotradebot.trade.ml.strategy;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.MlStrategyConfig;
import com.chicu.neurotradebot.trade.ml.TpSlPredictor;
import com.chicu.neurotradebot.trade.ml.TpSlResult;
import com.chicu.neurotradebot.trade.service.MarketDataService;
import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

@Component("ML_TPSL")
@RequiredArgsConstructor
public class MlTpSlStrategy {

    private final MarketDataService marketDataService;
    private final TpSlPredictor predictor;
    private final SpotTradeExecutor executor;

    public void execute(AiTradeSettings settings, Long chatId) {
        MlStrategyConfig cfg       = settings.getMlStrategyConfig();
        BigDecimal totalCapitalUsd = cfg.getTotalCapitalUsd();
        Duration lookback          = cfg.getLookbackPeriod();
        List<String> pairs         = settings.getPairs();
        if (pairs.isEmpty()) return;

        BigDecimal perPairUsd = totalCapitalUsd
            .divide(BigDecimal.valueOf(pairs.size()), 8, RoundingMode.DOWN);

        for (String symbol : pairs) {
            // 1) история
            List<Bar> bars = marketDataService.getHistoricalBars(
                symbol, lookback, 20, chatId
            );
            // 2) индикаторы
            var iv  = IndicatorCalculator.calculate(bars);
            var last = bars.get(bars.size() - 1);

            // 3) условия входа
            if (!(iv.getRsi() < cfg.getEntryRsiThreshold()
                  && last.getClose().doubleValue() < iv.getBbLower()
                  && last.getClose().compareTo(last.getOpen()) > 0)) {
                continue;
            }

            // 4) ML TP/SL
            TpSlResult ml = predictor.predict(iv, cfg.getPredictUrl());

            // 5) entryPrice и объём
            BigDecimal entryPrice = last.getClose();
            BigDecimal qty        = perPairUsd
                .divide(entryPrice, 8, RoundingMode.DOWN);

            // 6) абсолютные цены TP/SL
            BigDecimal tpPrice = entryPrice.multiply(
                BigDecimal.valueOf(1 + ml.getTpPercent() / 100))
                .setScale(last.getClose().scale(), RoundingMode.HALF_UP);
            BigDecimal slPrice = entryPrice.multiply(
                BigDecimal.valueOf(1 - ml.getSlPercent() / 100))
                .setScale(last.getClose().scale(), RoundingMode.HALF_UP);

            // 7) выставляем OCO-ордер
            executor.placeBracketOrder(chatId, symbol, qty, tpPrice, slPrice);
        }
    }
}

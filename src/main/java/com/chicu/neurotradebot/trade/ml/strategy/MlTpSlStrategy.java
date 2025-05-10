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
    private final TpSlPredictor    predictor;
    private final SpotTradeExecutor executor;

    /**
     * @param settings содержит:
     *   - settings.getScanInterval() — биржевой таймфрейм (например PT1M, PT1H)
     *   - settings.getMlStrategyConfig().getLookbackPeriod() — период для ML (напр. PT24H)
     *   - settings.getPairs() — ваш список пар
     */
    public void execute(AiTradeSettings settings, Long chatId) {
        MlStrategyConfig cfg       = settings.getMlStrategyConfig();
        BigDecimal totalCapitalUsd = cfg.getTotalCapitalUsd();
        double     entryRsiThresh  = cfg.getEntryRsiThreshold();
        Duration   scanInterval    = settings.getScanInterval();      // например PT1H
        Duration   lookbackPeriod  = cfg.getLookbackPeriod();        // например PT24H
        List<String> pairs         = settings.getPairs();
        if (pairs.isEmpty()) return;

        // считаем, сколько баров нужно: lookbackPeriod / scanInterval, минимум 20
        long lookbackSecs = lookbackPeriod.getSeconds();
        long intervalSecs = scanInterval.getSeconds();
        int  lookbackBars = (int) Math.max(lookbackSecs / intervalSecs, 20);

        BigDecimal perPairUsd = totalCapitalUsd
            .divide(BigDecimal.valueOf(pairs.size()), 8, RoundingMode.DOWN);

        for (String symbol : pairs) {
            // 1) история
            List<Bar> bars = marketDataService.getHistoricalBars(
                symbol, scanInterval, lookbackBars, chatId
            );
            if (bars == null || bars.size() < 20) {
                // Для расчёта индикаторов нужно минимум 20 баров
                continue;
            }

            // 2) индикаторы
            var iv   = IndicatorCalculator.calculate(bars);
            var last = bars.get(bars.size() - 1);

            // 3) условие входа
            if (!(iv.getRsi() < entryRsiThresh
                  && last.getClose().doubleValue() < iv.getBbLower()
                  && last.getClose().compareTo(last.getOpen()) > 0)) {
                continue;
            }

            // 4) TP/SL из ML
            TpSlResult ml = predictor.predict(iv, cfg.getPredictUrl());

            // 5) объём
            BigDecimal entryPrice = last.getClose();
            BigDecimal qty = perPairUsd
                .divide(entryPrice, 8, RoundingMode.DOWN);
            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // 6) абсолютные TP/SL
            BigDecimal tpPrice = entryPrice
                .multiply(BigDecimal.ONE.add(BigDecimal.valueOf(ml.getTpPercent()).divide(BigDecimal.valueOf(100))))
                .setScale(entryPrice.scale(), RoundingMode.HALF_UP);
            BigDecimal slPrice = entryPrice
                .multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(ml.getSlPercent()).divide(BigDecimal.valueOf(100))))
                .setScale(entryPrice.scale(), RoundingMode.HALF_UP);

            // 7) выставляем OCO‐ордер
            executor.placeBracketOrder(chatId, symbol, qty, tpPrice, slPrice);
        }
    }
}

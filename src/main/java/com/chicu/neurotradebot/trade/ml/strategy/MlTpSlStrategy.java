package com.chicu.neurotradebot.trade.ml.strategy;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.MlStrategyConfig;
import com.chicu.neurotradebot.trade.ml.TpSlPredictor;
import com.chicu.neurotradebot.trade.ml.TpSlResult;
import com.chicu.neurotradebot.trade.service.MarketDataService;
import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

@Component("ML_TPSL")
@RequiredArgsConstructor
@Slf4j
public class MlTpSlStrategy {

    // Минимальное число баров, необходимое для расчёта индикаторов (RSI14 + BB20 + ATR14)
    private static final int MIN_BARS_FOR_INDICATORS = 20;

    private final MarketDataService marketDataService;
    private final TpSlPredictor      predictor;
    private final SpotTradeExecutor  executor;

    public void execute(AiTradeSettings settings, Long chatId) {
        MlStrategyConfig cfg        = settings.getMlStrategyConfig();
        Duration scanInterval       = settings.getScanInterval();
        Duration lookbackPeriod     = cfg.getLookbackPeriod();
        List<String> pairs          = settings.getPairs();

        if (pairs.isEmpty()) {
            log.warn("ML_TPSL: ни одной пары не выбрано, выходим");
            return;
        }

        long millisInterval = scanInterval.toMillis();
        long millisLookback = lookbackPeriod.toMillis();
        if (millisInterval <= 0 || millisLookback <= 0) {
            log.error("ML_TPSL: неверные интервалы (scanInterval={} мс, lookbackPeriod={} мс), выходим",
                    millisInterval, millisLookback);
            return;
        }

        int lookbackBars = (int)(millisLookback / millisInterval);
        if (lookbackBars < MIN_BARS_FOR_INDICATORS) {
            log.warn("ML_TPSL: рассчитано lookbackBars={} < {}, используем {}",
                    lookbackBars, MIN_BARS_FOR_INDICATORS, MIN_BARS_FOR_INDICATORS);
            lookbackBars = MIN_BARS_FOR_INDICATORS;
        } else {
            log.info("ML_TPSL: lookbackPeriod={} мс, scanInterval={} мс → lookbackBars={}",
                    millisLookback, millisInterval, lookbackBars);
        }

        BigDecimal totalCapitalUsd = cfg.getTotalCapitalUsd();
        BigDecimal perPairUsd      = totalCapitalUsd
                .divide(BigDecimal.valueOf(pairs.size()), 8, RoundingMode.DOWN);

        for (String symbol : pairs) {
            List<Bar> bars;
            try {
                bars = marketDataService.getHistoricalBars(symbol, scanInterval, lookbackBars, chatId);
            } catch (Exception ex) {
                log.warn("ML_TPSL: не удалось получить {} баров для {}: {}", lookbackBars, symbol, ex.getMessage());
                continue;
            }
            if (bars.size() < MIN_BARS_FOR_INDICATORS) {
                log.info("ML_TPSL: получили {} баров для {}, а нужно {}, пропускаем",
                        bars.size(), symbol, MIN_BARS_FOR_INDICATORS);
                continue;
            }

            // 1) рассчитаем индикаторы
            var iv   = IndicatorCalculator.calculate(bars);
            var last = bars.get(bars.size() - 1);

            // 2) проверим условие входа
            boolean entryCond = iv.getRsi() < cfg.getEntryRsiThreshold()
                    && last.getClose().doubleValue() < iv.getBbLower()
                    && last.getClose().compareTo(last.getOpen()) > 0;
            if (!entryCond) {
                log.info("ML_TPSL: условие входа не выполнено для {}, пропускаем", symbol);
                continue;
            }

            // 3) запросим TP/SL у ML-сервиса
            TpSlResult ml;
            try {
                ml = predictor.predict(iv, cfg.getPredictUrl());
            } catch (Exception ex) {
                log.warn("ML_TPSL: ошибка ML для {}: {}", symbol, ex.getMessage());
                continue;
            }

            // 4) рассчитаем размер позиции
            BigDecimal entryPrice = last.getClose();
            BigDecimal rawQty     = perPairUsd.divide(entryPrice, 8, RoundingMode.DOWN);
            if (rawQty.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("ML_TPSL: rawQty={} для {} ≤0, пропускаем", rawQty, symbol);
                continue;
            }

            // 5) посчитаем абсолютные цены TP/SL
            BigDecimal tpPrice = entryPrice
                    .multiply(BigDecimal.ONE.add(BigDecimal.valueOf(ml.getTpPercent()).divide(BigDecimal.valueOf(100))))
                    .setScale(entryPrice.scale(), RoundingMode.HALF_UP);
            BigDecimal slPrice = entryPrice
                    .multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(ml.getSlPercent()).divide(BigDecimal.valueOf(100))))
                    .setScale(entryPrice.scale(), RoundingMode.HALF_UP);

            // 6) отправляем OCO-ордер
            try {
                executor.placeBracketOrder(chatId, symbol, rawQty, tpPrice, slPrice);
                log.info("ML_TPSL: OCO для {} qty={} TP={} SL={}", symbol, rawQty, tpPrice, slPrice);
            } catch (Exception ex) {
                log.error("ML_TPSL: не удалось отправить OCO для {}: {}", symbol, ex.getMessage());
            }
        }
    }
}

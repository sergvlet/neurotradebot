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

    private final MarketDataService marketDataService;
    private final TpSlPredictor    predictor;
    private final SpotTradeExecutor executor;


    public void execute(AiTradeSettings settings, Long chatId) {
        MlStrategyConfig cfg       = settings.getMlStrategyConfig();
        Duration scanInterval      = settings.getScanInterval();
        Duration lookbackPeriod    = cfg.getLookbackPeriod();
        List<String> pairs         = settings.getPairs();
        if (pairs.isEmpty()) {
            log.info("ML_TPSL: список пар пуст, пропускаем");
            return;
        }

        // рассчитываем число баров = lookbackPeriod / scanInterval, минимум 20
        long lookbackSecs = lookbackPeriod.getSeconds();
        long intervalSecs = scanInterval.getSeconds();
        int  lookbackBars = (int) Math.max(lookbackSecs / intervalSecs, 20);
        log.info("ML_TPSL: потребуется баров={}, интервал считывания={}", lookbackBars, scanInterval);

        BigDecimal perPairUsd = cfg.getTotalCapitalUsd()
                .divide(BigDecimal.valueOf(pairs.size()), 8, RoundingMode.DOWN);

        for (String symbol : pairs) {
            log.info("ML_TPSL: запрашиваем {} баров для {}", lookbackBars, symbol);
            List<Bar> bars = marketDataService.getHistoricalBars(symbol, scanInterval, lookbackBars, chatId);
            int received = bars == null ? 0 : bars.size();
            log.info("ML_TPSL: получено баров={} для {}", received, symbol);
            if (bars == null || bars.size() < 20) {
                log.info("ML_TPSL: недостаточно баров для {}, пропускаем", symbol);
                continue;
            }

            var iv   = IndicatorCalculator.calculate(bars);
            var last = bars.get(bars.size() - 1);
            log.info("ML_TPSL: индикаторы для {} → RSI={}, BB_низу={}, ATR={}, bodyRatio={}",
                    symbol, iv.getRsi(), iv.getBbLower(), iv.getAtr(), iv.getBodyRatio());

            boolean entryCond = iv.getRsi() < cfg.getEntryRsiThreshold()
                    && last.getClose().doubleValue() < iv.getBbLower()
                    && last.getClose().compareTo(last.getOpen()) > 0;
            log.info("ML_TPSL: условие входа для {} = {}", symbol, entryCond);
            if (!entryCond) {
                log.info("ML_TPSL: условие входа не выполнено для {}, пропускаем", symbol);
                continue;
            }

            TpSlResult ml = predictor.predict(iv, cfg.getPredictUrl());
            log.info("ML_TPSL: ML вернул для {} → TP={}%, SL={}%",
                    symbol, ml.getTpPercent(), ml.getSlPercent());

            BigDecimal entryPrice = last.getClose();
            BigDecimal qty = perPairUsd
                    .divide(entryPrice, 8, RoundingMode.DOWN);
            log.info("ML_TPSL: для {} рассчитаны entryPrice={} и qty={}", symbol, entryPrice, qty);
            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("ML_TPSL: объём ≤0 для {}, пропускаем", symbol);
                continue;
            }

            BigDecimal tpPrice = entryPrice
                    .multiply(BigDecimal.ONE.add(BigDecimal.valueOf(ml.getTpPercent()).divide(BigDecimal.valueOf(100))))
                    .setScale(entryPrice.scale(), RoundingMode.HALF_UP);
            BigDecimal slPrice = entryPrice
                    .multiply(BigDecimal.ONE.subtract(BigDecimal.valueOf(ml.getSlPercent()).divide(BigDecimal.valueOf(100))))
                    .setScale(entryPrice.scale(), RoundingMode.HALF_UP);
            log.info("ML_TPSL: выставляем OCO-ордер для {} → TP={}, SL={}", symbol, tpPrice, slPrice);

            executor.placeBracketOrder(chatId, symbol, qty, tpPrice, slPrice);
            log.info("ML_TPSL: ордер отправлен для {}", symbol);
        }
    }
}

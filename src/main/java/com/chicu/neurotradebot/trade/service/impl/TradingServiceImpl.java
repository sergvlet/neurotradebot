// src/main/java/com/chicu/neurotradebot/trade/service/impl/TradingServiceImpl.java
package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.enums.TradeMode;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.trade.ml.strategy.MlTpSlStrategy;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.risk.RiskManager;
import com.chicu.neurotradebot.trade.risk.RiskResult;
import com.chicu.neurotradebot.trade.service.MarketDataService;
import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import com.chicu.neurotradebot.trade.service.TradingService;
import com.chicu.neurotradebot.trade.service.TradingStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingServiceImpl implements TradingService {

    private final AiTradeSettingsService settingsService;
    private final MarketDataService marketDataService;
    private final Map<StrategyType, TradingStrategy> strategyMap;
    private final RiskManager riskManager;
    private final SpotTradeExecutor spotExecutor;
    private final MlTpSlStrategy mlTpSlStrategy;
    private final com.chicu.neurotradebot.trade.service.binance.BinanceClientProvider clientProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void executeCycle() {
        Long chatId = BotContext.getChatId();
        if (chatId == null) {
            log.warn("ChatId отсутствует в контексте — пропускаем цикл");
            return;
        }
        executeCycle(chatId);
    }

    @Override
    @Transactional
    public void executeCycle(Long chatId) {
        AiTradeSettings cfg = settingsService.getByChatId(chatId);
        if (!cfg.isEnabled() || cfg.getTradeMode() != TradeMode.SPOT) {
            log.info("Торговля отключена или не SPOT для chatId={}", chatId);
            return;
        }

        // === ML TP/SL стратегия ===
        if (cfg.getStrategies().contains(StrategyType.ML_TPSL)) {
            log.info("Запуск ML TP/SL стратегии для chatId={}", chatId);
            mlTpSlStrategy.execute(cfg, chatId);
            return;
        }

        // === Старая логика для обычных стратегий ===
        Duration interval = cfg.getScanInterval();
        Set<StrategyType> strategies = cfg.getStrategies();
        int needed = strategies.stream()
                .map(strategyMap::get)
                .filter(Objects::nonNull)
                .mapToInt(s -> s.requiredBars(cfg))
                .max()
                .orElse(0);

        if (needed <= 0) {
            log.warn("Нет валидных стратегий или requiredBars=0 — пропускаем");
            return;
        }

        for (String sym : cfg.getPairs()) {
            if (sym == null || sym.isBlank()) {
                log.warn("Пропускаем пустую пару из настроек");
                continue;
            }

            // 1) Исторические бары
            List<Bar> hist = marketDataService.getHistoricalBars(sym, interval, needed, chatId);
            if (hist.size() < needed) {
                log.warn("Недостаточно баров для {}: нужно={}, получили={}", sym, needed, hist.size());
                continue;
            }

            // 2) Цена входа
            BigDecimal entryPrice = hist.get(hist.size() - 1).getClose();
            if (entryPrice == null || entryPrice.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("entryPrice null или ≤0 ({}) для {} — пропускаем", entryPrice, sym);
                continue;
            }

            // 3) Генерация сигналов
            Signal finalSignal = Signal.HOLD;
            for (StrategyType st : strategies) {
                TradingStrategy strat = strategyMap.get(st);
                if (strat == null) continue;
                Signal s = strat.generateSignal(sym, hist, cfg);
                log.info("Стратегия {} дала сигнал {} для {}", st, s, sym);
                if (s == Signal.SELL) {
                    finalSignal = Signal.SELL;
                    break;
                }
                if (s == Signal.BUY) {
                    finalSignal = Signal.BUY;
                }
            }
            if (finalSignal == Signal.HOLD) {
                log.info("По всем стратегиям HOLD для {} (chatId={})", sym, chatId);
                continue;
            }

            // 4) Баланс и объём
            boolean isBuy = finalSignal == Signal.BUY;
            BigDecimal freeBalance = riskManager.getFreeBalance(chatId, sym, isBuy);
            if (freeBalance.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("freeBalance пустой или ≤0 ({}) для {} — пропускаем", freeBalance, sym);
                continue;
            }

            RiskResult rr = riskManager.calculate(cfg.getRiskConfig(), freeBalance, entryPrice);
            BigDecimal rawQty = rr.getQuantity();
            if (rawQty.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Для {} расчётный объём = {} ≤ 0, пропускаем", sym, rawQty);
                continue;
            }

            // 5) Коррекция по LOT_SIZE
            BigDecimal qty = adjustToStepSize(chatId, sym, rawQty);
            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("После коррекции LOT_SIZE rawQty={} → qty={} для {} — пропускаем", rawQty, qty, sym);
                continue;
            }

            // 6) Исполнение ордера
            log.info("Исполняем {} {} {} по цене {}", finalSignal, qty, sym, entryPrice);
            if (isBuy) {
                spotExecutor.buy(chatId, sym, qty);
            } else {
                spotExecutor.sell(chatId, sym, qty);
            }
        }
    }

    @Override
    @Transactional
    public void executeManualOrder(String symbol, boolean buy) {
        Long chatId = BotContext.getChatId();
        if (chatId == null) {
            log.warn("ChatId отсутствует в контексте — ручной ордер отменён");
            return;
        }
        AiTradeSettings cfg = settingsService.getForCurrentUser();

        int needed = strategyMap.values().stream()
                .filter(Objects::nonNull)
                .mapToInt(s -> s.requiredBars(cfg))
                .max().orElse(0);
        List<Bar> hist = marketDataService.getHistoricalBars(symbol, cfg.getScanInterval(), needed, chatId);
        if (hist.size() < needed) {
            log.warn("Недостаточно баров для ручного ордера {}: нужно={}, получили={}", symbol, needed, hist.size());
            return;
        }

        BigDecimal entryPrice = hist.get(hist.size() - 1).getClose();
        if (entryPrice == null || entryPrice.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("entryPrice null или ≤0 ({}) для ручного {}", entryPrice, symbol);
            return;
        }

        BigDecimal freeBalance = riskManager.getFreeBalance(chatId, symbol, buy);
        RiskResult rr = riskManager.calculate(cfg.getRiskConfig(), freeBalance, entryPrice);
        BigDecimal rawQty = rr.getQuantity();
        if (rawQty.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Ручной ордер {}: объём = {} ≤ 0, отменён", buy ? "BUY" : "SELL", rawQty);
            return;
        }

        BigDecimal qty = adjustToStepSize(chatId, symbol, rawQty);
        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("После коррекции LOT_SIZE rawQty={} → qty={} для ручного {} — отменён", rawQty, qty, symbol);
            return;
        }

        log.info("Ручной ордер {}: {} {} по цене {}", buy ? "BUY" : "SELL", qty, symbol, entryPrice);
        if (buy) {
            spotExecutor.buy(chatId, symbol, qty);
        } else {
            spotExecutor.sell(chatId, symbol, qty);
        }
    }

    /**
     * Округляет вниз qty до ближайшего разрешённого шага stepSize из фильтра LOT_SIZE.
     */
    private BigDecimal adjustToStepSize(Long chatId, String symbol, BigDecimal qty) {
        try {
            var client = clientProvider.getClientForUser(chatId);
            String infoJson = client.getExchangeInfo();
            var symbols   = objectMapper.readTree(infoJson).get("symbols");
            for (var symNode : symbols) {
                if (!symbol.equals(symNode.get("symbol").asText())) continue;
                for (var filter : symNode.get("filters")) {
                    if (!"LOT_SIZE".equals(filter.get("filterType").asText())) continue;
                    BigDecimal minQty   = new BigDecimal(filter.get("minQty").asText());
                    BigDecimal maxQty   = new BigDecimal(filter.get("maxQty").asText());
                    BigDecimal stepSize = new BigDecimal(filter.get("stepSize").asText());
                    if (qty.compareTo(minQty) < 0) return BigDecimal.ZERO;
                    BigDecimal steps = qty.divide(stepSize, 0, RoundingMode.DOWN);
                    BigDecimal adj   = steps.multiply(stepSize);
                    if (adj.compareTo(maxQty) > 0) {
                        steps = maxQty.divide(stepSize, 0, RoundingMode.DOWN);
                        adj   = steps.multiply(stepSize);
                    }
                    return adj;
                }
            }
        } catch (Exception ex) {
            log.error("Ошибка adjustToStepSize для {}: {}", symbol, ex.getMessage());
        }
        return BigDecimal.ZERO;
    }
}

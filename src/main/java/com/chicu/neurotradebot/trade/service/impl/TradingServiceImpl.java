package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.enums.TradeMode;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.risk.RiskManager;
import com.chicu.neurotradebot.trade.risk.RiskResult;
import com.chicu.neurotradebot.trade.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingServiceImpl implements TradingService {

    private final AiTradeSettingsService settingsService;
    private final MarketDataService marketDataService;
    private final java.util.Map<StrategyType, TradingStrategy> strategyMap;
    private final RiskManager riskManager;
    private final SpotTradeExecutor spotExecutor;
    private final AccountService accountService;

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
        if (!cfg.isEnabled()) {
            log.info("Торговля отключена для chatId={}", chatId);
            return;
        }
        if (cfg.getTradeMode() != TradeMode.SPOT) {
            log.info("Режим торговли не SPOT ({}), chatId={}", cfg.getTradeMode(), chatId);
            return;
        }

        Duration interval = cfg.getScanInterval();
        Set<StrategyType> strategies = cfg.getStrategies();
        Long userId = cfg.getUser().getId();

        // Вычисляем максимальное окно для всех выбранных стратегий (или 0, если нет валидных)
        int needed = strategies.stream()
                .map(strategyMap::get)
                .filter(Objects::nonNull)
                .mapToInt(strat -> strat.requiredBars(cfg))
                .max()
                .orElse(0);

        if (needed <= 0) {
            log.warn("Для chatId={} нет валидных стратегий или requiredBars=0 — пропускаем цикл", chatId);
            return;
        }

        for (String sym : cfg.getPairs()) {
            List<Bar> hist = marketDataService.getHistoricalBars(sym, interval, needed, chatId);
            if (hist.size() < needed) {
                log.warn("Недостаточно баров для {}: нужно={}, получили={} — пропускаем",
                        sym, needed, hist.size());
                continue;
            }

            // Объединяем сигналы: SELL превыше BUY, BUY — превыше HOLD
            Signal finalSignal = Signal.HOLD;
            for (StrategyType st : strategies) {
                TradingStrategy strat = strategyMap.get(st);
                if (strat == null) {
                    log.warn("Стратегия {} не найдена — пропускаем", st);
                    continue;
                }
                Signal s = strat.generateSignal(sym, hist, cfg);
                log.info("Стратегия {} дала сигнал {} по {}", st, s, sym);
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

            BigDecimal price = hist.get(hist.size() - 1).getClose();
            BigDecimal bal   = accountService.getFreeBalance(userId, sym.substring(sym.length() - 4));
            RiskResult rr    = riskManager.calculate(cfg.getRiskConfig(), bal, price);
            BigDecimal qty   = rr.getQuantity();

            // Пропускаем нулевой или отрицательный объём
            if (qty.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Для {} расчётный объём = {} ≤ 0, ордер пропускается", sym, qty);
                continue;
            }

            log.info("Исполняем {} {} {} по цене {}", finalSignal, qty, sym, price);
            if (finalSignal == Signal.BUY) {
                spotExecutor.buy(userId, sym, qty);
            } else {
                spotExecutor.sell(userId, sym, qty);
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

        Duration interval = cfg.getScanInterval();
        Set<StrategyType> strategies = cfg.getStrategies();
        Long userId = cfg.getUser().getId();

        int needed = strategies.stream()
                .map(strategyMap::get)
                .filter(Objects::nonNull)
                .mapToInt(strat -> strat.requiredBars(cfg))
                .max()
                .orElse(0);

        if (needed <= 0) {
            log.warn("Для ручного ордера нет валидных стратегий или requiredBars=0 — отмена");
            return;
        }

        List<Bar> hist = marketDataService.getHistoricalBars(symbol, interval, needed, chatId);
        if (hist.size() < needed) {
            log.warn("Недостаточно баров для ручного ордера {}: нужно={}, получили={} — userId={}",
                    symbol, needed, hist.size(), userId);
            return;
        }

        BigDecimal price = hist.get(hist.size() - 1).getClose();
        BigDecimal bal   = accountService.getFreeBalance(userId, symbol.substring(symbol.length() - 4));
        RiskResult rr    = riskManager.calculate(cfg.getRiskConfig(), bal, price);
        BigDecimal qty   = rr.getQuantity();

        if (qty.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Ручной ордер {}: объём = {} ≤ 0, отменён", buy ? "BUY" : "SELL", qty);
            return;
        }

        log.info("Ручной ордер {}: {} {} по цене {}",
                buy ? "BUY" : "SELL", qty, symbol, price);
        if (buy) spotExecutor.buy(userId, symbol, qty);
        else     spotExecutor.sell(userId, symbol, qty);
    }
}

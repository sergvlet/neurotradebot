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
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingServiceImpl implements TradingService {

    private final AiTradeSettingsService settingsService;
    private final MarketDataService       marketDataService;
    /** Вместо одной стратегии — карта всех доступных */
    private final Map<StrategyType, TradingStrategy> strategyMap;
    private final RiskManager             riskManager;
    private final SpotTradeExecutor spotExecutor;
    private final AccountService          accountService;

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

        // Для расчёта окна берём максимальный «needed» среди выбранных стратегий
        int needed = strategies.stream()
                .mapToInt(st -> strategyMap.get(st).requiredBars(cfg))
                .max().orElse(0);

        for (String sym : cfg.getPairs()) {
            List<Bar> hist = marketDataService.getHistoricalBars(sym, interval, needed, chatId);
            if (hist.size() < needed) {
                log.warn("Недостаточно баров для {}: нужно={}, получили={} — пропускаем",
                        sym, needed, hist.size());
                continue;
            }

            // объединяем сигналы: если хоть одна SELL — SELL; иначе если хоть одна BUY — BUY
            Signal finalSignal = Signal.HOLD;
            for (StrategyType st : strategies) {
                TradingStrategy strat = strategyMap.get(st);
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

            log.info("Исполняем {} {} {} по цене {}", finalSignal, rr.getQuantity(), sym, price);
            if (finalSignal == Signal.BUY) {
                spotExecutor.buy(userId, sym, rr.getQuantity());
            } else {
                spotExecutor.sell(userId, sym, rr.getQuantity());
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
                .mapToInt(st -> strategyMap.get(st).requiredBars(cfg))
                .max().orElse(0);

        List<Bar> hist = marketDataService.getHistoricalBars(symbol, interval, needed, chatId);
        if (hist.size() < needed) {
            log.warn("Недостаточно баров для ручного ордера {}: нужно={}, получили={} — userId={}",
                    symbol, needed, hist.size(), userId);
            return;
        }

        BigDecimal price = hist.get(hist.size() - 1).getClose();
        BigDecimal bal   = accountService.getFreeBalance(userId, symbol.substring(symbol.length() - 4));
        RiskResult rr    = riskManager.calculate(cfg.getRiskConfig(), bal, price);

        log.info("Ручной ордер {}: {} {} по цене {}",
                buy ? "BUY" : "SELL", rr.getQuantity(), symbol, price);
        if (buy) {
            spotExecutor.buy(userId, symbol, rr.getQuantity());
        } else {
            spotExecutor.sell(userId, symbol, rr.getQuantity());
        }
    }
}

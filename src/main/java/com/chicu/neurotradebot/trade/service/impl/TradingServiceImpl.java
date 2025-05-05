// src/main/java/com/chicu/neurotradebot/trade/service/impl/TradingServiceImpl.java
package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.entity.RiskConfig;
import com.chicu.neurotradebot.enums.Bar;
import com.chicu.neurotradebot.enums.TradeMode;

import com.chicu.neurotradebot.service.AiTradeSettingsService;


import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.risk.RiskManager;
import com.chicu.neurotradebot.trade.risk.RiskResult;
import com.chicu.neurotradebot.trade.service.*;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис исполнения торговых операций:
 * - автоматический цикл по сигналам стратегии
 * - ручные ордера по запросу пользователя
 */
@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final AiTradeSettingsService settingsService;

    private final TradingStrategy rsiMacdStrategy;
    private final RiskManager riskManager;
    private final SpotTradeExecutor spotExecutor;
    private final AccountService accountService;

    /**
     * Автоматический торговый цикл:
     * - запускается каждое время, заданное в настройках (scanInterval)
     * - для каждой валютной пары:
     *   1) загружает историю баров через MarketDataService
     *   2) генерирует сигнал RSI+MACD через TradingStrategy
     *   3) рассчитывает объём и уровни SL/TP через RiskManager
     *   4) исполняет ордер на спотовом рынке через SpotTradeExecutor
     */
    @Override
    @Scheduled(fixedDelayString = "#{@aiTradeSettingsService.getScanIntervalMillis()}")
    public void executeCycle() {
        AiTradeSettings cfg = settingsService.getForCurrentUser();
        if (!cfg.isEnabled() || cfg.getTradeMode() != TradeMode.SPOT) {
            return;
        }
        RiskConfig riskCfg = cfg.getRiskConfig();

        for (String symbol : cfg.getPairs()) {
            var rsiCfg = cfg.getRsiMacdConfig();
            int neededBars = rsiCfg.getMacdSlow() + rsiCfg.getMacdSignal() + 1;

            // 1) Получаем историю баров
            List<Bar> history = marketDataService.getHistoricalBars(
                    symbol,
                    cfg.getScanInterval().toString(),
                    neededBars
            );

            // 2) Генерируем торговый сигнал
            Signal signal = rsiMacdStrategy.generateSignal(symbol, history, cfg);
            if (signal == Signal.HOLD) {
                continue;
            }

            // 3) Последняя цена закрытия
            BigDecimal entryPrice = history.get(history.size() - 1).getClose();

            // 4) Получаем свободный баланс котируемой валюты (например, "USDT")
            String quoteAsset = symbol.substring(symbol.length() - 4);
            BigDecimal freeBalance = accountService.getFreeBalance(
                    cfg.getUser().getId(), quoteAsset
            );

            // 5) Рассчитываем объём, стоп-лосс и тейк-профит
            RiskResult rr = riskManager.calculate(riskCfg, freeBalance, entryPrice);

            // 6) Исполняем ордер BUY или SELL
            if (signal == Signal.BUY) {
                spotExecutor.buy(cfg.getUser().getId(), symbol, rr.getQuantity());
            } else {
                spotExecutor.sell(cfg.getUser().getId(), symbol, rr.getQuantity());
            }

            // TODO: выставить OCO-ордера для SL/TP
        }
    }

    /**
     * Ручная торговля по запросу пользователя:
     * - загружает минимальный объём баров
     * - игнорирует сигнал стратегии и исполняет ордер по параметру buy
     *
     * @param symbol тикер пары, например "BTCUSDT"
     * @param buy    true — купить, false — продать
     */
    @Override
    public void executeManualOrder(String symbol, boolean buy) {
        AiTradeSettings cfg = settingsService.getForCurrentUser();
        var rsiCfg = cfg.getRsiMacdConfig();
        int neededBars = rsiCfg.getMacdSlow() + rsiCfg.getMacdSignal() + 1;

        List<Bar> history = marketDataService.getHistoricalBars(
                symbol,
                cfg.getScanInterval().toString(),
                neededBars
        );

        BigDecimal entryPrice = history.get(history.size() - 1).getClose();
        String quoteAsset = symbol.substring(symbol.length() - 4);
        BigDecimal freeBalance = accountService.getFreeBalance(
                cfg.getUser().getId(), quoteAsset
        );

        RiskResult rr = riskManager.calculate(cfg.getRiskConfig(), freeBalance, entryPrice);

        if (buy) {
            spotExecutor.buy(cfg.getUser().getId(), symbol, rr.getQuantity());
        } else {
            spotExecutor.sell(cfg.getUser().getId(), symbol, rr.getQuantity());
        }
    }
}

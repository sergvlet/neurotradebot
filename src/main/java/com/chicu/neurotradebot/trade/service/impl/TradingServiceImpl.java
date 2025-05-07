package com.chicu.neurotradebot.trade.service.impl;

import com.chicu.neurotradebot.entity.AiTradeSettings;

import com.chicu.neurotradebot.entity.Bar;
import com.chicu.neurotradebot.entity.RsiMacdConfig;
import com.chicu.neurotradebot.enums.TradeMode;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.BotContext;
import com.chicu.neurotradebot.trade.model.Signal;
import com.chicu.neurotradebot.trade.risk.RiskManager;
import com.chicu.neurotradebot.trade.risk.RiskResult;
import com.chicu.neurotradebot.trade.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final AiTradeSettingsService settingsService;
    private final MarketDataService marketDataService;
    private final TradingStrategy rsiMacdStrategy;
    private final RiskManager riskManager;
    private final SpotTradeExecutor spotExecutor;
    private final AccountService accountService;

    @Override
    public void executeCycle() {
        // этот метод больше не используется внутри планировщика
        throw new UnsupportedOperationException("Use executeCycle(chatId)");
    }

    @Override
    @Transactional
    public void executeCycle(Long chatId) {
        AiTradeSettings cfg = settingsService.getByChatId(chatId);
        if (!cfg.isEnabled() || cfg.getTradeMode() != TradeMode.SPOT) {
            return;
        }

        RsiMacdConfig rsiCfg = cfg.getRsiMacdConfig();
        if (rsiCfg == null) {
            rsiCfg = new RsiMacdConfig();
        }
        int neededBars = rsiCfg.getMacdSlow() + rsiCfg.getMacdSignal() + 1;
        Long userId = cfg.getUser().getId();

        for (String symbol : cfg.getPairs()) {
            List<Bar> history = marketDataService.getHistoricalBars(
                    symbol,
                    cfg.getScanInterval(),                     neededBars,
                    chatId
            );

            Signal signal = rsiMacdStrategy.generateSignal(symbol, history, cfg);
            if (signal == Signal.HOLD) continue;

            BigDecimal entryPrice = history.get(history.size() - 1).getClose();
            String quoteAsset = symbol.substring(symbol.length() - 4);
            BigDecimal freeBalance = accountService.getFreeBalance(userId, quoteAsset);

            RiskResult rr = riskManager.calculate(cfg.getRiskConfig(), freeBalance, entryPrice);

            if (signal == Signal.BUY) {
                spotExecutor.buy(userId, symbol, rr.getQuantity());
            } else {
                spotExecutor.sell(userId, symbol, rr.getQuantity());
            }
        }
    }

    @Override
    public void executeManualOrder(String symbol, boolean buy) {
        AiTradeSettings cfg = settingsService.getForCurrentUser();
        // ... аналогично, здесь chatId внутри getForCurrentUser()
        Long userId = cfg.getUser().getId();
        RsiMacdConfig rsiCfg = cfg.getRsiMacdConfig();
        if (rsiCfg == null) rsiCfg = new RsiMacdConfig();
        int neededBars = rsiCfg.getMacdSlow() + rsiCfg.getMacdSignal() + 1;

        List<Bar> history = marketDataService.getHistoricalBars(
                symbol,
                cfg.getScanInterval(),                 neededBars,
                BotContext.getChatId()
        );

        BigDecimal entryPrice = history.get(history.size() - 1).getClose();
        String quoteAsset = symbol.substring(symbol.length() - 4);
        BigDecimal freeBalance = accountService.getFreeBalance(userId, quoteAsset);

        RiskResult rr = riskManager.calculate(cfg.getRiskConfig(), freeBalance, entryPrice);

        if (buy) spotExecutor.buy(userId, symbol, rr.getQuantity());
        else     spotExecutor.sell(userId, symbol, rr.getQuantity());
    }
}

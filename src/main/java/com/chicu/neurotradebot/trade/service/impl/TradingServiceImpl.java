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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingServiceImpl implements TradingService {

    private final AiTradeSettingsService settingsService;
    private final MarketDataService       marketDataService;
    private final TradingStrategy         rsiMacdStrategy;
    private final RiskManager             riskManager;
    private final SpotTradeExecutor       spotExecutor;
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

        RsiMacdConfig c = cfg.getRsiMacdConfig();
        if (c == null) {
            log.warn("RsiMacdConfig не задан для chatId={} — ставлю дефолт", chatId);
            c = RsiMacdConfig.builder()
                    .macdFast(12)
                    .macdSlow(26)
                    .macdSignal(9)
                    .rsiPeriod(14)
                    .rsiLower(BigDecimal.valueOf(30))
                    .rsiUpper(BigDecimal.valueOf(70))
                    .build();
            cfg.setRsiMacdConfig(c);
            settingsService.save(cfg);
        }

        int needed = c.getMacdSlow() + c.getMacdSignal() + 1;
        Duration interval = cfg.getScanInterval();
        Long userId = cfg.getUser().getId();

        for (String sym : cfg.getPairs()) {
            List<Bar> hist = marketDataService.getHistoricalBars(sym, interval, 300, chatId);

            if (hist.size() < needed) {
                log.warn("Недостаточно баров для {}: нужно={}, получили={} — пропускаем", sym, needed, hist.size());
                continue;
            }

            Signal sig;
            try {
                sig = rsiMacdStrategy.generateSignal(sym, hist, cfg);
            } catch (Exception ex) {
                log.error("Ошибка при расчёте сигнала для {}: {}", sym, ex.toString());
                continue;
            }

            log.info("Сигнал для {} = {} (chatId={})", sym, sig, chatId);
            if (sig == Signal.HOLD) {
                continue;
            }

            BigDecimal price = hist.get(hist.size() - 1).getClose();
            String quote    = sym.substring(sym.length() - 4);
            BigDecimal bal   = accountService.getFreeBalance(userId, quote);
            RiskResult rr    = riskManager.calculate(cfg.getRiskConfig(), bal, price);

            log.info("Исполняем {} {} {} по цене {}", sig, rr.getQuantity(), sym, price);
            if (sig == Signal.BUY) {
                spotExecutor.buy(userId, sym, rr.getQuantity());
            } else {
                spotExecutor.sell(userId, sym, rr.getQuantity());
            }
        }
    }

    @Override
    public void executeManualOrder(String symbol, boolean buy) {
        Long chatId = BotContext.getChatId();
        if (chatId == null) {
            log.warn("ChatId отсутствует в контексте — ручной ордер отменён");
            return;
        }

        AiTradeSettings cfg = settingsService.getForCurrentUser();
        RsiMacdConfig c = cfg.getRsiMacdConfig();
        if (c == null) {
            log.warn("RsiMacdConfig не задан — ручной ордер отменён");
            return;
        }

        int needed = c.getMacdSlow() + c.getMacdSignal() + 1;
        Duration interval = cfg.getScanInterval();
        Long userId = cfg.getUser().getId();

        List<Bar> hist = marketDataService.getHistoricalBars(symbol, interval, needed, chatId);
        if (hist.size() < needed) {
            log.warn("Недостаточно баров для ручного ордера {}: нужно={}, получили={} — userId={}",
                    symbol, needed, hist.size(), userId);
            return;
        }

        BigDecimal price = hist.get(hist.size() - 1).getClose();
        String quote     = symbol.substring(symbol.length() - 4);
        BigDecimal bal    = accountService.getFreeBalance(userId, quote);
        RiskResult rr     = riskManager.calculate(cfg.getRiskConfig(), bal, price);

        log.info("Ручной ордер {}: {} {} по цене {}",
                buy ? "BUY" : "SELL", rr.getQuantity(), symbol, price);
        if (buy) {
            spotExecutor.buy(userId, symbol, rr.getQuantity());
        } else {
            spotExecutor.sell(userId, symbol, rr.getQuantity());
        }
    }
}

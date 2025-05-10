package com.chicu.neurotradebot.trade.service.binance;

import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceSpotTradeExecutor implements SpotTradeExecutor {

    private final BinanceClientProvider clientProvider;

    @Override
    public void buy(Long chatId, String symbol, BigDecimal quantity) {
        if (StringUtils.isBlank(symbol) || quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("BUY skip: symbol='{}', qty={} chatId={}", symbol, quantity, chatId);
            return;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("symbol",   symbol);
            params.put("side",     "BUY");
            params.put("type",     "MARKET");
            params.put("quantity", quantity);

            clientProvider.getClientForUser(chatId).newOrder(params);
            log.info("BUY market order: {} {} chatId={}", symbol, quantity, chatId);
        } catch (Exception ex) {
            log.error("BUY failed: {} {} chatId={}", symbol, quantity, chatId, ex);
        }
    }

    @Override
    public void sell(Long chatId, String symbol, BigDecimal quantity) {
        if (StringUtils.isBlank(symbol) || quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("SELL skip: symbol='{}', qty={} chatId={}", symbol, quantity, chatId);
            return;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("symbol",   symbol);
            params.put("side",     "SELL");
            params.put("type",     "MARKET");
            params.put("quantity", quantity);

            clientProvider.getClientForUser(chatId).newOrder(params);
            log.info("SELL market order: {} {} chatId={}", symbol, quantity, chatId);
        } catch (Exception ex) {
            log.error("SELL failed: {} {} chatId={}", symbol, quantity, chatId, ex);
        }
    }

    @Override
    public void placeBracketOrder(Long chatId,
                                  String symbol,
                                  BigDecimal quantity,
                                  BigDecimal tpPrice,
                                  BigDecimal slPrice) {
        if (StringUtils.isBlank(symbol) || quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("OCO skip: symbol='{}', qty={} chatId={}", symbol, quantity, chatId);
            return;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("symbol",               symbol);
            params.put("side",                 "BUY");  // или "SELL"
            params.put("type",                 "OCO");
            params.put("quantity",             quantity);
            params.put("price",                tpPrice);
            params.put("stopPrice",            slPrice);
            params.put("stopLimitPrice",       slPrice);
            params.put("stopLimitTimeInForce", "GTC");

            clientProvider.getClientForUser(chatId).newOrder(params);
            log.info("OCO order placed: {} qty={} TP@{} SL@{} chatId={}",
                    symbol, quantity, tpPrice, slPrice, chatId);
        } catch (Exception ex) {
            log.error("OCO failed: {} {} chatId={}", symbol, quantity, chatId, ex);
        }
    }
}

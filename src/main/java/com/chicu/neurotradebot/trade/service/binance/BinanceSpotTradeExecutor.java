package com.chicu.neurotradebot.trade.service.binance;

import com.chicu.neurotradebot.trade.service.SpotTradeExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class BinanceSpotTradeExecutor implements SpotTradeExecutor {

    private final BinanceClientProvider clientProvider;

    /**
     * @param chatId  Telegram-chatId пользователя (то же, что вы прокидываете в clientProvider)
     * @param symbol  торговая пара, например "BTCUSDT"
     * @param quantity  объём в базовой валюте (для BUY это кол-во котируемой валюты / price)
     */
    @Override
    public void buy(Long chatId, String symbol, BigDecimal quantity) {
        if (StringUtils.isBlank(symbol)) {
            log.warn("BUY: пустая символ-пара для chatId={}", chatId);
            return;
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("BUY: некорректный объём {} для {} (chatId={})", quantity, symbol, chatId);
            return;
        }
        try {
            // обратите внимание: передаём именно chatId, а не внутренний userId
            var client = clientProvider.getClientForUser(chatId);
            client.newOrder(symbol, "BUY", quantity);
            log.info("BUY order placed: {} {}, chatId={}", symbol, quantity, chatId);
        } catch (IllegalStateException ex) {
            log.warn("Не удалось разместить BUY-ордер {} {} для chatId={}: {}", symbol, quantity, chatId, ex.getMessage());
        } catch (Exception ex) {
            log.error("Ошибка при попытке BUY {} {} для chatId={}: {}", symbol, quantity, chatId, ex.toString());
        }
    }

    @Override
    public void sell(Long chatId, String symbol, BigDecimal quantity) {
        if (StringUtils.isBlank(symbol)) {
            log.warn("SELL: пустая символ-пара для chatId={}", chatId);
            return;
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("SELL: некорректный объём {} для {} (chatId={})", quantity, symbol, chatId);
            return;
        }
        try {
            var client = clientProvider.getClientForUser(chatId);
            client.newOrder(symbol, "SELL", quantity);
            log.info("SELL order placed: {} {}, chatId={}", symbol, quantity, chatId);
        } catch (IllegalStateException ex) {
            log.warn("Не удалось разместить SELL-ордер {} {} для chatId={}: {}", symbol, quantity, chatId, ex.getMessage());
        } catch (Exception ex) {
            log.error("Ошибка при попытке SELL {} {} для chatId={}: {}", symbol, quantity, chatId, ex.toString());
        }
    }
}

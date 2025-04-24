package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.telegram.callback.BotCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TradingStatusService {

    // Сохраняем messageId активного сообщения, чтобы потом обновлять
    private final StatusRegistryService statusRegistryService;

    private final Map<Long, Boolean> tradingStatus = new ConcurrentHashMap<>();

    /**
     * Регистрирует статус торговли для пользователя
     */
    public void enableTrading(Long chatId) {
        tradingStatus.put(chatId, true);
    }

    public void disableTrading(Long chatId) {
        tradingStatus.put(chatId, false);
    }

    /**
     * Проверяет, разрешена ли торговля для пользователя
     *
     * @param chatId идентификатор пользователя
     * @return true, если торговля разрешена, иначе false
     */
    public boolean isTradingEnabled(Long chatId) {
        return tradingStatus.getOrDefault(chatId, false);
    }

    public void register(Long chatId, Integer messageId) {
        statusRegistryService.save(chatId, messageId);
    }

    public InlineKeyboardMarkup getActiveTradeStatusKeyboard() {
        List<List<InlineKeyboardButton>> buttons = List.of(
                List.of(InlineKeyboardButton.builder()
                        .text("🔄 Обновить статус")
                        .callbackData(BotCallback.UPDATE_TRADE_STATUS.getValue())
                        .build()),
                List.of(InlineKeyboardButton.builder()
                        .text("🔙 Назад")
                        .callbackData(BotCallback.TRADING_MENU.getValue())
                        .build())
        );
        return InlineKeyboardMarkup.builder().keyboard(buttons).build();
    }
}

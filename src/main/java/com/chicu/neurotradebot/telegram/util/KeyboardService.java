package com.chicu.neurotradebot.telegram.util;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.telegram.callback.BotCallback;
import com.chicu.neurotradebot.trade.model.TradeMode;
import com.chicu.neurotradebot.trade.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class KeyboardService {

    private final UserSettingsService userSettingsService;

    public InlineKeyboardMarkup getMainMenu(Long chatId) {
        return buildKeyboard(List.of(
                List.of(createButton("🚀 Начать", BotCallback.START_TRADE.getValue())),
                List.of(createButton("⚙️ Настройки", BotCallback.SETTINGS.getValue())),
                List.of(createButton("💳 Подписка", BotCallback.SUBSCRIBE.getValue())),
                List.of(createButton("🤖 О боте", BotCallback.BOT_INFO.getValue()))
        ));
    }

    public InlineKeyboardMarkup getSettingsMenu(Long chatId) {
        var settings = userSettingsService.getOrCreate(chatId);
        String symbol = settings.getExchangeSymbol();
        String tf = settings.getTimeframe();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        buttons.add(List.of(createButton("🧠 Стратегии", BotCallback.STRATEGY_MENU.getValue())));
        buttons.add(List.of(createButton("🧪 Режим торговли", BotCallback.SELECT_MODE.getValue())));
        buttons.add(List.of(createButton("💵 Лимит сделки", BotCallback.TRADE_LIMIT.getValue())));
        buttons.add(List.of(createButton("📊 Символ: " + symbol, BotCallback.EXCHANGE_SELECT.getValue())));
        buttons.add(List.of(createButton("⏱ Таймфрейм: " + tf, BotCallback.TIMEFRAME_SELECT.getValue())));

        return appendBackButton(buttons);
    }

    public InlineKeyboardMarkup getStrategySelectionMenu(Set<AvailableStrategy> selected) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (AvailableStrategy strategy : AvailableStrategy.values()) {
            boolean isSelected = selected.contains(strategy);
            String prefix = isSelected ? "✅ " : "☑️ ";
            String text = prefix + strategy.getTitle();
            String callback = BotCallback.TOGGLE_STRATEGY.getValue() + ":" + strategy.name();
            rows.add(List.of(createButton(text, callback)));
        }

        return appendBackButton(rows);
    }

    public InlineKeyboardMarkup getModeSelectionMenu(Long chatId, TradeMode selected) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (TradeMode mode : TradeMode.values()) {
            String prefix = mode == selected ? "✅ " : "☑️ ";
            String text = prefix + mode.getTitle();
            String callback = BotCallback.TOGGLE_MODE.getValue() + ":" + mode.name();
            buttons.add(List.of(createButton(text, callback)));
        }

        buttons.add(List.of(createButton("📊 Показать график", BotCallback.SHOW_STRATEGY_GRAPH.getValue())));

        return appendBackButton(buttons);
    }

    public InlineKeyboardMarkup getSymbolSelectionMenu() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<String> symbols = List.of("BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT");

        for (String symbol : symbols) {
            buttons.add(List.of(createButton(symbol, BotCallback.SYMBOL_SET.getValue() + ":" + symbol)));
        }

        return appendBackButton(buttons);
    }

    public InlineKeyboardMarkup getTimeframeSelectionMenu() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<String> timeframes = List.of("1m", "5m", "15m", "1h", "4h", "1d");

        for (String tf : timeframes) {
            buttons.add(List.of(createButton(tf, BotCallback.TIMEFRAME_SET.getValue() + ":" + tf)));
        }

        return appendBackButton(buttons);
    }

    public InlineKeyboardMarkup appendBackButton(List<List<InlineKeyboardButton>> buttons) {
        buttons.add(List.of(createButton("🔙 Назад", BotCallback.BACK.getValue())));
        return buildKeyboard(buttons);
    }

    public InlineKeyboardMarkup buildKeyboard(List<List<InlineKeyboardButton>> buttons) {
        return InlineKeyboardMarkup.builder().keyboard(buttons).build();
    }

    public InlineKeyboardButton createButton(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}

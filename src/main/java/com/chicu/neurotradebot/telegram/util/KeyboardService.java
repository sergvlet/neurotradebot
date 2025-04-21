package com.chicu.neurotradebot.telegram.util;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.telegram.callback.BotCallback;
import com.chicu.neurotradebot.trade.model.TradeMode;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class KeyboardService {

    public InlineKeyboardMarkup getMainMenu(Long chatId) {
        return buildKeyboard(List.of(
                List.of(createButton("🚀 Начать", BotCallback.START_TRADE.getValue())),
                List.of(createButton("⚙️ Настройки", BotCallback.SETTINGS.getValue())),
                List.of(createButton("💳 Подписка", BotCallback.SUBSCRIBE.getValue())),
                List.of(createButton("🤖 О боте", BotCallback.BOT_INFO.getValue()))
        ));
    }

    public InlineKeyboardMarkup getSettingsMenu(Long chatId) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

//        buttons.add(List.of(createButton("🔄 Выбор биржи", BotCallback.EXCHANGE_SELECT.getValue())));
//        buttons.add(List.of(createButton("🔑 API ключи", BotCallback.API_KEYS.getValue())));
//        buttons.add(List.of(createButton("🔔 Уведомления", BotCallback.NOTIFICATIONS.getValue())));
        buttons.add(List.of(createButton("🧠 Стратегии", BotCallback.STRATEGY_MENU.getValue())));
        buttons.add(List.of(createButton("🧪 Режим торговли", BotCallback.SELECT_MODE.getValue())));
        buttons.add(List.of(createButton("💵 Лимит сделки", BotCallback.TRADE_LIMIT.getValue())));

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

        return appendBackButton(buttons);
    }



}

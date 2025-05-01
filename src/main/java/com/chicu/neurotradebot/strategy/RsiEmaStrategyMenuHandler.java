package com.chicu.neurotradebot.strategy;

import com.chicu.neurotradebot.service.StrategySettingsService;
import com.chicu.neurotradebot.session.InputStage;
import com.chicu.neurotradebot.session.UserSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RsiEmaStrategyMenuHandler {

    private final StrategySettingsService strategySettingsService;

    public Object showStrategyOptions(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        RsiEmaStrategy strategy = strategySettingsService.loadStrategy(chatId);

        String message = """
                🎯 *Стратегия: RSI + EMA*

                🧮 *Текущие параметры:*
                • EMA короткая: `%d`
                • EMA длинная: `%d`
                • RSI период: `%d`
                • RSI BUY < `%.1f`
                • RSI SELL > `%.1f`
                • Свечей для анализа: `%d`

                Выберите действие:
                """.formatted(
                strategy.getEmaShort(),
                strategy.getEmaLong(),
                strategy.getRsiPeriod(),
                strategy.getRsiBuyThreshold(),
                strategy.getRsiSellThreshold(),
                strategy.getMinCandles()
        );

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(message)
                .replyMarkup(buildStrategyMenu())
                .parseMode("Markdown")
                .build();
    }

    public Object resetToDefault(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        strategySettingsService.resetToDefault(chatId);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("✅ Параметры стратегии *RSI + EMA* сброшены на стандартные.")
                .replyMarkup(buildStrategyMenu())
                .parseMode("Markdown")
                .build();
    }

    public Object handleCallback(Update update) {
        String data = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();

        return switch (data) {
            case "ai_strategy_settings" ->
                    buildEditStage(update, InputStage.AI_STRATEGY_MENU, "Выберите параметр для изменения:", buildParameterEditMenu());

            case "ai_set_rsi_period" ->
                    setInputStage(update, InputStage.AI_STRATEGY_SET_RSI_PERIOD, "Введите новое значение RSI периода:");
            case "ai_set_ema_short" ->
                    setInputStage(update, InputStage.AI_STRATEGY_SET_EMA_SHORT, "Введите новое значение EMA SHORT:");
            case "ai_set_ema_long" ->
                    setInputStage(update, InputStage.AI_STRATEGY_SET_EMA_LONG, "Введите новое значение EMA LONG:");
            case "ai_set_rsi_buy" ->
                    setInputStage(update, InputStage.AI_STRATEGY_SET_RSI_BUY, "Введите порог RSI для покупки:");
            case "ai_set_rsi_sell" ->
                    setInputStage(update, InputStage.AI_STRATEGY_SET_RSI_SELL, "Введите порог RSI для продажи:");
            case "ai_set_limit" ->
                    setInputStage(update, InputStage.AI_STRATEGY_SET_LIMIT, "Введите минимальное количество свечей:");

            default -> null;
        };
    }

    public Object handleText(Update update) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String text = message.getText().trim();

        InputStage stage = UserSessionManager.getInputStage(chatId);
        RsiEmaStrategy strategy = strategySettingsService.loadStrategy(chatId);

        try {
            switch (stage) {
                case AI_STRATEGY_SET_RSI_PERIOD -> strategy.setRsiPeriod(Integer.parseInt(text));
                case AI_STRATEGY_SET_EMA_SHORT -> strategy.setEmaShort(Integer.parseInt(text));
                case AI_STRATEGY_SET_EMA_LONG -> strategy.setEmaLong(Integer.parseInt(text));
                case AI_STRATEGY_SET_RSI_BUY -> strategy.setRsiBuyThreshold(Double.parseDouble(text));
                case AI_STRATEGY_SET_RSI_SELL -> strategy.setRsiSellThreshold(Double.parseDouble(text));
                case AI_STRATEGY_SET_LIMIT -> strategy.setMinCandles(Integer.parseInt(text));
                default -> {
                    return null;
                }
            }
        } catch (NumberFormatException e) {
            return "❌ Неверный формат. Введите число.";
        }

        strategySettingsService.saveStrategy(chatId, strategy);
        UserSessionManager.setInputStage(chatId, InputStage.NONE);
        return showStrategyOptionsFromText(chatId, message.getMessageId());
    }

    private Object showStrategyOptionsFromText(long chatId, int messageId) {
        RsiEmaStrategy strategy = strategySettingsService.loadStrategy(chatId);

        String message = """
                🎯 *Стратегия: RSI + EMA обновлена*

                • EMA короткая: `%d`
                • EMA длинная: `%d`
                • RSI период: `%d`
                • RSI BUY < `%.1f`
                • RSI SELL > `%.1f`
                • Свечей для анализа: `%d`

                Выберите действие:
                """.formatted(
                strategy.getEmaShort(),
                strategy.getEmaLong(),
                strategy.getRsiPeriod(),
                strategy.getRsiBuyThreshold(),
                strategy.getRsiSellThreshold(),
                strategy.getMinCandles()
        );

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(message)
                .replyMarkup(buildStrategyMenu())
                .parseMode("Markdown")
                .build();
    }

    private Object setInputStage(Update update, InputStage stage, String prompt) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        UserSessionManager.setInputStage(chatId, stage);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("✏️ " + prompt)
                .parseMode("Markdown")
                .build();
    }

    private Object buildEditStage(Update update, InputStage stage, String text, InlineKeyboardMarkup markup) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        int messageId = update.getCallbackQuery().getMessage().getMessageId();
        UserSessionManager.setInputStage(chatId, stage);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(text)
                .replyMarkup(markup)
                .parseMode("Markdown")
                .build();
    }

    private InlineKeyboardMarkup buildStrategyMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("⚙️ Настроить параметры", "ai_strategy_settings")),
                        List.of(button("🧹 Сбросить на дефолт", "ai_strategy_reset")),
                        List.of(button("🔙 Назад", "ai_back_main"))
                ))
                .build();
    }

    private InlineKeyboardMarkup buildParameterEditMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(button("📏 RSI период", "ai_set_rsi_period")),
                        List.of(button("📉 RSI BUY < значение", "ai_set_rsi_buy")),
                        List.of(button("📈 RSI SELL > значение", "ai_set_rsi_sell")),
                        List.of(button("💹 EMA SHORT", "ai_set_ema_short")),
                        List.of(button("📊 EMA LONG", "ai_set_ema_long")),
                        List.of(button("🧱 Мин. свечей", "ai_set_limit")),
                        List.of(button("🔙 Назад", "ai_strategy_main"))
                ))
                .build();
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}

// src/main/java/com/chicu/neurotradebot/telegram/handler/TextMessageHandler.java
package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.ConfigWaiting;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.MlTpSlConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TextMessageHandler implements MessageHandler {

    private final AiTradeSettingsService settingsService;
    private final TelegramSender sender;
    private final MlTpSlConfigMenuBuilder mlTpSlConfigMenu;

    @Override
    public boolean canHandle(Update upd) {
        // обрабатываем только текстовые
        return upd.hasMessage() && upd.getMessage().hasText();
    }

    @Override
    public void handle(Update upd) {
        Long chatId = upd.getMessage().getChatId();
        String text = upd.getMessage().getText().trim();
        Integer lastMsgId = upd.getMessage().getMessageId() - 1; // предполагаем, что menu было отправлено прямо перед
        AiTradeSettings settings = settingsService.getByChatId(chatId);

        ConfigWaiting waiting = settingsService.getWaiting(chatId);
        if (waiting != null) {
            switch (waiting) {
                // ==== Капитал USD ====
                case ML_DEC_CAPITAL, ML_INC_CAPITAL -> {
                    BigDecimal currCap = settings.getMlStrategyConfig().getTotalCapitalUsd();
                    BigDecimal delta   = BigDecimal.valueOf(waiting == ConfigWaiting.ML_DEC_CAPITAL ? -10 : 10);
                    BigDecimal updated = currCap.add(delta).max(BigDecimal.ZERO);
                    settings.getMlStrategyConfig().setTotalCapitalUsd(updated);
                    settingsService.save(settings);
                    settingsService.clearWaiting(chatId);
                    mlTpSlConfigMenu.buildOrEditMenu(chatId, lastMsgId);
                }
                // ==== Порог RSI ====
                case ML_DEC_RSI, ML_INC_RSI -> {
                    double curr = settings.getMlStrategyConfig().getEntryRsiThreshold();
                    double d    = waiting == ConfigWaiting.ML_DEC_RSI ? -1 : 1;
                    settings.getMlStrategyConfig().setEntryRsiThreshold(curr + d);
                    settingsService.save(settings);
                    settingsService.clearWaiting(chatId);
                    mlTpSlConfigMenu.buildOrEditMenu(chatId, lastMsgId);
                }
                // ==== Lookback Period (в часах) ====
                case ML_DEC_LOOKBACK, ML_INC_LOOKBACK -> {
                    Duration curr = settings.getMlStrategyConfig().getLookbackPeriod();
                    Duration d    = Duration.ofHours(waiting == ConfigWaiting.ML_DEC_LOOKBACK ? -1 : 1);
                    Duration updated = curr.plus(d).isNegative() ? Duration.ZERO : curr.plus(d);
                    settings.getMlStrategyConfig().setLookbackPeriod(updated);
                    settingsService.save(settings);
                    settingsService.clearWaiting(chatId);
                    mlTpSlConfigMenu.buildOrEditMenu(chatId, lastMsgId);
                }
                // ==== Predict URL ====
                case ML_SET_URL -> {
                    settings.getMlStrategyConfig().setPredictUrl(text);
                    settingsService.save(settings);
                    settingsService.clearWaiting(chatId);
                    mlTpSlConfigMenu.buildOrEditMenu(chatId, lastMsgId);
                }
                default -> {
                    // если вдруг другое состояние — очистим и вернём меню
                    settingsService.clearWaiting(chatId);
                    mlTpSlConfigMenu.buildOrEditMenu(chatId, lastMsgId);
                }
            }
            return;
        }

        // Если мы не в режиме ожидания — можно обрабатывать другие текстовые команды здесь
        // например, /start, главное меню и т.п.
    }
}

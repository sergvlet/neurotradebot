package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.AccessControlService;
import com.chicu.neurotradebot.user.entity.ExchangeSettings;
import com.chicu.neurotradebot.user.repository.ExchangeSettingsRepository;
import com.chicu.neurotradebot.user.repository.UserRepository;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
@RequiredArgsConstructor
public class ToggleTestTradingHandler {

    private final UserRepository userRepository;
    private final ExchangeSettingsRepository exchangeSettingsRepository;
    private final StartMenuBuilder startMenuBuilder;
    private final AccessControlService accessControlService;

    public EditMessageText handle(long chatId, Integer messageId) {
        if (!accessControlService.hasActiveSubscription(chatId)) {
            return EditMessageText.builder()
                    .chatId(String.valueOf(chatId))
                    .messageId(messageId)
                    .text("""
                  ⛔ У вас нет активной подписки.

                  Пожалуйста, нажмите 👤 *Подписка* и выберите подходящий тариф.
                  """)
                    .parseMode("Markdown")
                    .build();
        }

        var user = userRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ExchangeSettings settings = exchangeSettingsRepository
                .findByUserIdAndExchange(user.getId(), "BINANCE")
                .orElseGet(() -> {
                    ExchangeSettings newSettings = new ExchangeSettings();
                    newSettings.setUser(user);
                    newSettings.setExchange("BINANCE");
                    return newSettings;
                });

        settings.setUseTestnet(true);
        exchangeSettingsRepository.save(settings);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("✅ Вы переключились на *тестовую торговлю (Testnet)* для Binance.")
                .replyMarkup(startMenuBuilder.buildMainMenu())
                .parseMode("Markdown")
                .build();
    }
}

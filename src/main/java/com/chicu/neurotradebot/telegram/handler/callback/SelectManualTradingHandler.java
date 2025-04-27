package com.chicu.neurotradebot.telegram.handler.callback;

import com.chicu.neurotradebot.subscription.service.AccessControlService;
import com.chicu.neurotradebot.user.entity.User;
import com.chicu.neurotradebot.user.repository.UserRepository;
import com.chicu.neurotradebot.telegram.handler.menu.StartMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component
@RequiredArgsConstructor
public class SelectManualTradingHandler {

    private final UserRepository userRepository;
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

        userRepository.findById(chatId).ifPresent(user -> {
            user.setTradingMode(User.TradingMode.MANUAL);
            userRepository.save(user);
        });

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text("✅ Вы выбрали *Ручную торговлю*.\nТеперь вы будете сами выбирать сделки.")
                .replyMarkup(startMenuBuilder.buildMainMenu())
                .parseMode("Markdown")
                .build();
    }
}

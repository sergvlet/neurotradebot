package com.chicu.neurotradebot.controller;

import com.chicu.neurotradebot.view.StartMenuBuilder;
import com.chicu.neurotradebot.view.SubscriptionMenuBuilder;
import com.chicu.neurotradebot.model.User;
import com.chicu.neurotradebot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionHandler {

    private final UserRepository userRepository;
    private final SubscriptionMenuBuilder subscriptionMenuBuilder;
    private final StartMenuBuilder startMenuBuilder;

    public Object handle(Update update) {
        var callbackQuery = update.getCallbackQuery();
        long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String data = callbackQuery.getData();

        User user = userRepository.findById(chatId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(chatId);
                    newUser.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newExpiry;

        if ("subscribe_trial".equals(data)) {
            if (user.isTrialUsed()) {
                return EditMessageText.builder()
                        .chatId(String.valueOf(chatId))
                        .messageId(messageId)
                        .text("""
                              ⚠️ Вы уже использовали бесплатный триал.

                              Пожалуйста, выберите платный тариф для продолжения работы:
                              """)
                        .replyMarkup(subscriptionMenuBuilder.buildSubscriptionMenu())
                        .parseMode("Markdown")
                        .build();
            }
            newExpiry = now.plusDays(10);
            user.setTrialUsed(true);
        } else if ("subscribe_1m".equals(data)) {
            newExpiry = now.plusMonths(1);
        } else if ("subscribe_3m".equals(data)) {
            newExpiry = now.plusMonths(3);
        } else if ("subscribe_6m".equals(data)) {
            newExpiry = now.plusMonths(6);
        } else if ("subscribe_1y".equals(data)) {
            newExpiry = now.plusYears(1);
        } else {
            return null;
        }

        user.setSubscriptionStartAt(now);
        user.setSubscriptionEndAt(newExpiry);
        userRepository.save(user);

        return EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId(messageId)
                .text(String.format("""
                      ✅ Подписка активирована!

                      Действует до: *%s*

                      Выберите действие в меню ниже:
                      """, newExpiry.toLocalDate()))
                .replyMarkup(startMenuBuilder.buildMainMenu())
                .parseMode("Markdown")
                .build();
    }
}

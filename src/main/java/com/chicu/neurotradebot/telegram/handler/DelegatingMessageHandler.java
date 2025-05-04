package com.chicu.neurotradebot.telegram.handler;

import com.chicu.neurotradebot.entity.User;
import com.chicu.neurotradebot.entity.UserInputState;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DelegatingMessageHandler implements MessageHandler {

    private final List<TextInputAwareMenu> textInputMenus;
    private final UserService userService;
    private final AiTradeSettingsService settingsService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userService.getOrCreate(chatId);
        var settings = settingsService.getOrCreate(user);
        UserInputState state = settings.getInputState();

        log.info("📥 Получен текст от chatId={} | state={}", chatId, state);

        if (state == null || state == UserInputState.NONE) {
            log.info("ℹ️ Ввод текста не ожидается (state: {})", state);
            return;
        }

        textInputMenus.stream()
                .filter(menu -> menu.supports(state))
                .findFirst()
                .ifPresentOrElse(
                        menu -> {
                            log.info("✉️ Делегация текстового ввода в {}", menu.getClass().getSimpleName());
                            menu.handleText(update);
                        },
                        () -> log.warn("❌ Нет меню, поддерживающего состояние ввода: {}", state)
                );
    }
}

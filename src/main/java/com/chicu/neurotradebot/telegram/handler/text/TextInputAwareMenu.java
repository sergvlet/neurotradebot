// src/main/java/com/chicu/neurotradebot/telegram/handler/text/TextInputAwareMenu.java
package com.chicu.neurotradebot.telegram.handler.text;

import com.chicu.neurotradebot.enums.UserInputState;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик текстового ввода, привязанный к конкретным состояниям UserInputState.
 */
public interface TextInputAwareMenu {
    /** Для каких состояний этот обработчик должен сработать */
    boolean supports(UserInputState state);

    /** Обработать пришедшее текстовое сообщение */
    void handleText(Update update);
}

// src/main/java/com/chicu/neurotradebot/telegram/handler/MenuDefinition.java
package com.chicu.neurotradebot.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import java.util.Set;

public interface MenuDefinition {
    Set<String> keys();               // какие callbackData обрабатываем
    String title();                   // текст заголовка
    InlineKeyboardMarkup markup(Long chatId);    // клавиатура
}

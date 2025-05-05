// src/main/java/com/chicu/neurotradebot/service/UserService.java
package com.chicu.neurotradebot.service;

// импортим Telegram-User
import org.telegram.telegrambots.meta.api.objects.User;

public interface UserService {
    /**
     * Создаёт или обновляет пользователя на /start,
     * заполняя данные из Telegram API.
     *
     * @param telegramUserId id пользователя в Телеграме
     * @param telegramUser   объект User из TelegramBots API
     * @return локальную сущность User
     */
    com.chicu.neurotradebot.entity.User getOrCreate(Long telegramUserId,
                                                    User telegramUser);

    /**
     * Просто возвращает или создаёт пользователя по telegramUserId,
     * без изменения других полей.
     */
    com.chicu.neurotradebot.entity.User getOrCreate(Long telegramUserId);
}

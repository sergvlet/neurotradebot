package com.chicu.neurotradebot.service;

import com.chicu.neurotradebot.entity.User;

/**
 * Сервис для работы с сущностью User.
 */
public interface UserService {
    /**
     * Создаёт или обновляет пользователя на /start,
     * заполняя все данные из Telegram API.
     */
    User getOrCreate(Long telegramUserId,
                     org.telegram.telegrambots.meta.api.objects.User from);

    /**
     * Просто возвращает или создаёт пользователя по telegramUserId,
     * без изменения других полей.
     */
    User getOrCreate(Long telegramUserId);

    /**
     * Обновляет телефон в любое время.
     */
    User updatePhoneNumber(Long telegramUserId, String phoneNumber);
}

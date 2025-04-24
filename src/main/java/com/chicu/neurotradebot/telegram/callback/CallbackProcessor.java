package com.chicu.neurotradebot.telegram.callback;

import org.telegram.telegrambots.meta.bots.AbsSender;

/**
 * Интерфейс для обработки колбеков в Telegram-боте.
 * Каждый конкретный обработчик должен реализовывать логику обработки соответствующих колбеков.
 */
public interface CallbackProcessor {

    /**
     * Возвращает тип колбека, с которым работает обработчик.
     * Используется для определения, какой обработчик должен быть вызван.
     *
     * @return тип колбека из BotCallback
     */
    BotCallback callback();

    /**
     * Метод для обработки логики, связанной с конкретным колбеком.
     * Например, обновление UI, выполнение торговой логики, отправка сообщений и т. д.
     *
     * @param chatId      идентификатор чата, с которым связан колбек
     * @param messageId   идентификатор сообщения, к которому привязан колбек
     * @param callbackData данные колбека, связанные с его состоянием
     * @param sender      объект AbsSender для отправки сообщений
     */
    void process(Long chatId, Integer messageId, String callbackData, AbsSender sender);
}

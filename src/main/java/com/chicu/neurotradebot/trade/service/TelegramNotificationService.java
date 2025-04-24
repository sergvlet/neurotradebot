package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import com.chicu.neurotradebot.trade.enums.Signal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final MessageUtils messageUtils;

    // Кэш для хранения AbsSender по chatId
    private final Map<Long, AbsSender> senderCache = new ConcurrentHashMap<>();

    /**
     * Регистрирует AbsSender для пользователя
     */
    public void registerSender(Long chatId, AbsSender sender) {
        senderCache.put(chatId, sender);
    }

    /**
     * Отправка сообщения
     */
    public void notify(Long chatId, String message) {
        AbsSender sender = senderCache.get(chatId);
        if (sender != null) {
            // Отправляем сообщение, если sender найден
            messageUtils.sendMessage(chatId, message, sender);
        } else {
            // Если sender не найден, выводим ошибку
            System.out.println("AbsSender для chatId " + chatId + " не найден.");
        }
    }

    /**
     * Отправка уведомления о торговом сигнале
     */
    public void sendTradeNotification(Long chatId, Signal signal) {
        // Формируем текст уведомления в зависимости от сигнала
        String text = switch (signal) {
            case BUY -> "🟢 Сигнал: ПОКУПКА";
            case SELL -> "🔴 Сигнал: ПРОДАЖА";
            case HOLD -> "⚪️ Сигнал: ДЕРЖАТЬ";
        };

        // Вызываем метод notify для отправки сообщения
        notify(chatId, text);
    }
}

package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.telegram.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final MessageUtils messageUtils;

    // В будущем сюда можно подключить полноценный статус, чтобы обновлять одно сообщение
    private final Map<Long, AbsSender> senderCache = new ConcurrentHashMap<>();

    public void registerSender(Long chatId, AbsSender sender) {
        senderCache.put(chatId, sender);
    }

    public void notify(Long chatId, String message) {
        AbsSender sender = senderCache.get(chatId);
        if (sender != null) {
            messageUtils.sendMessage(chatId, message, sender);
        }
    }
}

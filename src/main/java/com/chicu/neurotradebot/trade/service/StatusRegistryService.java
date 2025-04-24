package com.chicu.neurotradebot.trade.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatusRegistryService {

    // Храним соответствие: chatId -> messageId
    private final Map<Long, Integer> activeStatusMessages = new ConcurrentHashMap<>();

    public void save(Long chatId, Integer messageId) {
        activeStatusMessages.put(chatId, messageId);
    }

    public Integer getMessageId(Long chatId) {
        return activeStatusMessages.get(chatId);
    }

    public void clear(Long chatId) {
        activeStatusMessages.remove(chatId);
    }

    public boolean hasMessage(Long chatId) {
        return activeStatusMessages.containsKey(chatId);
    }
}

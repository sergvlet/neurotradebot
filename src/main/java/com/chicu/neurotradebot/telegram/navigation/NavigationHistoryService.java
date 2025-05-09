package com.chicu.neurotradebot.telegram.navigation;

import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NavigationHistoryService {
    // для каждого chatId — стек ключей меню
    private final Map<Long, Deque<String>> history = new ConcurrentHashMap<>();

    /** Запомнить, что открыто меню с ключом `menuKey` */
    public void push(Long chatId, String menuKey) {
        history
            .computeIfAbsent(chatId, id -> new ArrayDeque<>())
            .push(menuKey);
    }

    /**
     * Убрать текущее меню и вернуть ключ предыдущего.
     * Если истории нет или один элемент, вернёт null.
     */
    public String popPrevious(Long chatId) {
        Deque<String> stack = history.get(chatId);
        if (stack == null || stack.isEmpty()) return null;
        // удаляем текущее
        stack.pop();
        // возвращаем предыдущий (если есть)
        return stack.isEmpty() ? null : stack.peek();
    }

    /** Полностью очистить историю этого чата */
    public void clear(Long chatId) {
        history.remove(chatId);
    }
}

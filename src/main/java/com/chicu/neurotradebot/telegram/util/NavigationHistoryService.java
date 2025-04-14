package com.chicu.neurotradebot.telegram.util;

import com.chicu.neurotradebot.telegram.callback.BotCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NavigationHistoryService {

    private final Map<Long, Deque<BotCallback>> history = new HashMap<>();

    public void push(Long chatId, BotCallback callback) {
        history.computeIfAbsent(chatId, k -> new ArrayDeque<>()).push(callback);
    }

    public BotCallback pop(Long chatId) {
        Deque<BotCallback> stack = history.getOrDefault(chatId, new ArrayDeque<>());
        if (!stack.isEmpty()) {
            stack.pop(); // remove current
            return stack.peek(); // return previous
        }
        return BotCallback.MAIN_MENU;
    }

    public void reset(Long chatId) {
        history.remove(chatId);
    }

    public BotCallback getCurrent(Long chatId) {
        return history.getOrDefault(chatId, new ArrayDeque<>()).peek();
    }
}

package com.chicu.neurotradebot.telegram.callback;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CallbackFactory {

    private static final Logger log = LoggerFactory.getLogger(CallbackFactory.class);
    private final Map<BotCallback, CallbackProcessor> processorMap;

    public CallbackFactory(List<CallbackProcessor> callbackProcessors) {
        this.processorMap = callbackProcessors.stream()
                .collect(Collectors.toMap(CallbackProcessor::callback, Function.identity()));
    }

    public CallbackProcessor getProcessor(BotCallback callback) {
        CallbackProcessor processor = processorMap.get(callback);
        if (processor == null) {
            log.error("Не найден обработчик для callback: {}", callback);
            throw new IllegalArgumentException("Callback processor not found for: " + callback);
        }
        return processor;
    }
}

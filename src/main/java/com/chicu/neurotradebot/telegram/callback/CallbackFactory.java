package com.chicu.neurotradebot.telegram.callback;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component

public class CallbackFactory {

    private final Map<BotCallback, CallbackProcessor> processorMap;

    public CallbackFactory(List<CallbackProcessor> callbackProcessors) {
        this.processorMap = callbackProcessors.stream()
                .collect(Collectors.toMap(CallbackProcessor::callback, Function.identity()));
    }

    public CallbackProcessor getProcessor(BotCallback callback) {
        return processorMap.get(callback);
    }
}

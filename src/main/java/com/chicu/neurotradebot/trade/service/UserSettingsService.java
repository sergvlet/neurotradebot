package com.chicu.neurotradebot.trade.service;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.trade.enums.Exchange;
import com.chicu.neurotradebot.trade.enums.TradeMode;
import com.chicu.neurotradebot.trade.enums.TradeType;
import com.chicu.neurotradebot.trade.model.UserSettings;
import com.chicu.neurotradebot.trade.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsRepository repository;

    // ⏳ Ожидание текстового ввода (тип действия)
    private final Map<Long, String> waitingInputs = new HashMap<>();

    // 💾 Временное хранилище данных (например, API Key перед Secret Key)
    private final Map<Long, Map<String, String>> tempData = new HashMap<>();

    // 📌 Получить или создать настройки пользователя
    public UserSettings getOrCreate(Long chatId) {
        return repository.findByChatId(chatId)
                .orElseGet(() -> {
                    UserSettings settings = new UserSettings();
                    settings.setChatId(chatId);
                    settings.setStrategies(new HashSet<>());
                    settings.setTradeType(TradeType.AI);  // По умолчанию AI торговля
                    return repository.save(settings);
                });
    }

    // 🧠 Управление стратегиями
    public void toggleStrategy(Long chatId, AvailableStrategy strategy) {
        UserSettings settings = getOrCreate(chatId);
        Set<AvailableStrategy> strategies = settings.getStrategies();

        if (strategies.contains(strategy)) {
            strategies.remove(strategy);
        } else {
            strategies.add(strategy);
        }

        settings.setStrategies(strategies);
        repository.save(settings);
    }

    // Получить все выбранные стратегии для AI
    public Set<AvailableStrategy> getSelectedStrategies(Long chatId) {
        return getOrCreate(chatId).getStrategies();
    }

    // 💼 Режим торговли
    public void setTradeMode(Long chatId, TradeMode mode) {
        UserSettings settings = getOrCreate(chatId);
        settings.setTradeMode(mode);
        repository.save(settings);
    }

    public TradeMode getTradeMode(Long chatId) {
        return getOrCreate(chatId).getTradeMode();
    }

    // ⌨️ Ожидание текстового ввода
    public void setWaitingForInput(Long chatId, String inputType) {
        waitingInputs.put(chatId, inputType);
    }

    public String getWaitingFor(Long chatId) {
        return waitingInputs.get(chatId);
    }

    public boolean isWaitingFor(Long chatId, String inputType) {
        return inputType.equals(waitingInputs.get(chatId));
    }

    public void clearWaiting(Long chatId) {
        waitingInputs.remove(chatId);
    }

    // 💾 Временное хранение данных
    public void setTemp(Long chatId, String key, String value) {
        tempData.computeIfAbsent(chatId, k -> new HashMap<>()).put(key, value);
    }

    public String getTemp(Long chatId, String key) {
        return tempData.getOrDefault(chatId, new HashMap<>()).get(key);
    }

    public void clearTemp(Long chatId, String key) {
        Map<String, String> userTemp = tempData.get(chatId);
        if (userTemp != null) {
            userTemp.remove(key);
        }
    }

    // 💵 Лимит
    public void setTradeLimit(Long chatId, Double limit) {
        UserSettings settings = getOrCreate(chatId);
        settings.setTradeLimit(String.valueOf(limit));
        repository.save(settings);
    }

    // 📊 Символ
    public void setExchangeSymbol(Long chatId, String symbol) {
        UserSettings settings = getOrCreate(chatId);
        settings.setExchangeSymbol(symbol);
        repository.save(settings);
    }

    // ⏱ Таймфрейм
    public void setTimeframe(Long chatId, String tf) {
        UserSettings settings = getOrCreate(chatId);
        settings.setTimeframe(tf);
        repository.save(settings);
    }

    // 📈 Биржа
    public void setExchange(Long chatId, Exchange exchange) {
        UserSettings settings = getOrCreate(chatId);
        settings.setExchange(exchange);
        repository.save(settings);
    }

    public void setTradeType(Long chatId, TradeType type) {
        UserSettings settings = getOrCreate(chatId);
        settings.setTradeType(type);
        repository.save(settings);
    }

    public TradeType getTradeType(Long chatId) {
        return getOrCreate(chatId).getTradeType();
    }

    // 💡 Ручная стратегия
    public void setSelectedManualStrategy(Long chatId, AvailableStrategy strategy) {
        UserSettings settings = getOrCreate(chatId);
        settings.setSelectedManualStrategy(strategy);
        repository.save(settings);
    }

    public AvailableStrategy getSelectedManualStrategy(Long chatId) {
        return getOrCreate(chatId).getSelectedManualStrategy();
    }
    // Метод для сохранения настроек пользователя
    public void save(UserSettings userSettings) {
        repository.save(userSettings);
    }

}

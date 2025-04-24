package com.chicu.neurotradebot.trade.model;

import com.chicu.neurotradebot.ai.strategy.AvailableStrategy;
import com.chicu.neurotradebot.ai.strategy.config.AdxConfig;
import com.chicu.neurotradebot.ai.strategy.config.StrategyConfig;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StrategyConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private UserSettings userSettings; // Связь с UserSettings

    @Enumerated(EnumType.STRING)
    private AvailableStrategy strategy; // Тип стратегии

    @Lob
    private String configData; // Здесь можно хранить JSON или сериализованный объект конфигурации

    // Конструктор и методы для работы с конфигурацией
    public StrategyConfigEntity(UserSettings userSettings, AvailableStrategy strategy, StrategyConfig config) {
        this.userSettings = userSettings;
        this.strategy = strategy;
        this.configData = serializeConfig(config); // Сериализуем конфигурацию в строку (например, JSON)
    }

    // Метод для десериализации конфигурации
    public StrategyConfig getConfig() {
        return deserializeConfig(configData); // Преобразуем строку обратно в объект
    }

    private String serializeConfig(StrategyConfig config) {
        // Пример: сериализация объекта в строку
        // Можно использовать библиотеку, например, Jackson, для этого
        // Для упрощения оставляем как строку
        return config.toString(); 
    }

    private StrategyConfig deserializeConfig(String configData) {
        // Пример: десериализация строки обратно в объект
        // Можно использовать Jackson для этого
        return new AdxConfig(); // Вернем конкретный тип конфигурации
    }
}

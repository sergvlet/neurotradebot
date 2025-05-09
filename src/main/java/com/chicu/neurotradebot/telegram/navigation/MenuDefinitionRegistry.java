// src/main/java/com/chicu/neurotradebot/telegram/navigation/MenuDefinitionRegistry.java
package com.chicu.neurotradebot.telegram.navigation;

import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Собирает все MenuDefinition и индексирует их по ключам.
 */
@Component
public class MenuDefinitionRegistry {

    private final List<MenuDefinition> definitions;
    private final Map<String, MenuDefinition> registry = new HashMap<>();

    public MenuDefinitionRegistry(List<MenuDefinition> definitions) {
        this.definitions = definitions;
    }

    /** После создания бина, заполняем registry: для каждого MenuDefinition берём все его ключи */
    @PostConstruct
    public void init() {
        for (MenuDefinition def : definitions) {
            for (String key : def.keys()) {
                registry.put(key, def);
            }
        }
    }

    /** Возвращает MenuDefinition по ключу */
    public MenuDefinition get(String key) {
        return registry.get(key);
    }
}

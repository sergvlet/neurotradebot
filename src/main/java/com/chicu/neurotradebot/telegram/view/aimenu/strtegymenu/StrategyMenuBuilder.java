// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/StrategyMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StrategyMenuBuilder implements MenuDefinition {

    private final AiTradeSettingsService settingsService;

    @Override
    public Set<String> keys() {
        // именно этим ключом меню регистрируется в диспетчере
        return Set.of("ai_strategies");
    }

    @Override
    public String title() {
        return "Выберите стратегии:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        AiTradeSettings cfg = settingsService.getByChatId(chatId);
        Set<StrategyType> selected = cfg.getStrategies();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (StrategyType st : StrategyType.values()) {
            boolean isOn = selected.contains(st);

            InlineKeyboardButton toggleBtn = InlineKeyboardButton.builder()
                .text(isOn ? "✅" : "☐")
                .callbackData("toggle_strat_" + st.name())
                .build();

            InlineKeyboardButton configBtn = InlineKeyboardButton.builder()
                .text(st.getDisplayName())
                .callbackData("config_strat_" + st.name())
                .build();

            rows.add(List.of(toggleBtn, configBtn));
        }

        // кнопка «Назад»
        rows.add(List.of(
            InlineKeyboardButton.builder()
                .text("⬅️ Назад")
                .callbackData("apply_network_settings_ai")
                .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}

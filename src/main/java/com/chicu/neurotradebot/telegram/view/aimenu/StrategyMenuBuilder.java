// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/StrategyMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class StrategyMenuBuilder implements MenuDefinition {

    private final AiTradeSettingsService settingsService;

    public StrategyMenuBuilder(AiTradeSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Override
    public Set<String> keys() {
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
            String text = (isOn ? "✅ " : "☐ ") + st.name();
            rows.add(List.of(
                InlineKeyboardButton.builder()
                    .text(text)
                    .callbackData("toggle_strat_" + st.name())
                    .build()
            ));
        }
        // кнопка «Сохранить и назад»
        rows.add(List.of(
            InlineKeyboardButton.builder()
                .text("⬅️ Назад")
                .callbackData("ai_control")
                .build()
        ));

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }
}

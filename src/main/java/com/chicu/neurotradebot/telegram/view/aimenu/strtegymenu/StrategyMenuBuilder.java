// src/main/java/com/chicu/neurotradebot/telegram/view/aimenu/strtegymenu/StrategyMenuBuilder.java
package com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu;

import com.chicu.neurotradebot.entity.AiTradeSettings;
import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.handler.MenuDefinition;
import com.chicu.neurotradebot.telegram.navigation.NavigationHistoryService;
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
    private final NavigationHistoryService history;

    @Override
    public Set<String> keys() {
        // ключ, по которому это меню регистрируется в NavigationHistory и MenuRegistry
        return Set.of("ai_strategies");
    }

    @Override
    public String title() {
        return "Выберите стратегии и режим ML TP/SL:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        // 1) сохраняем в истории факт входа в это меню:
        history.push(chatId, "ai_strategies");

        AiTradeSettings cfg = settingsService.getByChatId(chatId);
        Set<StrategyType> selected = cfg.getStrategies();
        boolean mlEnabled = cfg.isUseMlTpSl();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // 2) Первая строка — переключатель ML TP/SL и кнопка настроек ML
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text(mlEnabled ? "✅ ML TP/SL" : "☐ ML TP/SL")
                        .callbackData("toggle_ml_tp_sl")
                        .build(),
                InlineKeyboardButton.builder()
                        .text("⚙️ Настройки ML")
                        .callbackData("config_ml_tp_sl")
                        .build()
        ));

        // 3) Основные стратегии с чекбоксами
        for (StrategyType st : StrategyType.values()) {
            rows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(selected.contains(st) ? "✅" : "☐")
                            .callbackData("toggle_strat_" + st.name())
                            .build(),
                    InlineKeyboardButton.builder()
                            .text(st.getDisplayName())
                            .callbackData("config_strat_" + st.name())
                            .build()
            ));
        }

        // 4) Универсальная кнопка «Назад»
        rows.add(List.of(
                InlineKeyboardButton.builder()
                        .text("⬅️ Назад")
                        .callbackData("back")
                        .build()
        ));

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}

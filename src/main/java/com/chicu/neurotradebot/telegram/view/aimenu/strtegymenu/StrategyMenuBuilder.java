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
        return Set.of("ai_strategies");
    }

    @Override
    public String title() {
        return "Выберите стратегии и режим ML TP/SL:";
    }

    @Override
    public InlineKeyboardMarkup markup(Long chatId) {
        AiTradeSettings cfg = settingsService.getByChatId(chatId);
        Set<StrategyType> sel = cfg.getStrategies();
        boolean ml = cfg.isUseMlTpSl();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        // ML TP/SL toggle row
        rows.add(List.of(
          InlineKeyboardButton.builder()
              .text(ml ? "✅ ML TP/SL" : "☐ ML TP/SL")
              .callbackData("toggle_ml_tp_sl")
              .build(),
          InlineKeyboardButton.builder()
              .text("⚙️ Настройки ML")
              .callbackData("config_ml_tp_sl")
              .build()
        ));

        // остальные стратегии
        for (StrategyType st : StrategyType.values()) {
            rows.add(List.of(
              InlineKeyboardButton.builder()
                  .text(sel.contains(st) ? "✅" : "☐")
                  .callbackData("toggle_strat_" + st.name())
                  .build(),
              InlineKeyboardButton.builder()
                  .text(st.getDisplayName())
                  .callbackData("config_strat_" + st.name())
                  .build()
            ));
        }

        // назад
        rows.add(List.of(
          InlineKeyboardButton.builder()
              .text("⬅️ Назад")
              .callbackData("main_menu")
              .build()
        ));

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }
}

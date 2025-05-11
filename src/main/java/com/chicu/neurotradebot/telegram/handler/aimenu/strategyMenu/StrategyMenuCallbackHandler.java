// src/main/java/com/chicu/neurotradebot/telegram/handler/aimenu/strategyMenu/StrategyMenuCallbackHandler.java
package com.chicu.neurotradebot.telegram.handler.aimenu.strategyMenu;

import com.chicu.neurotradebot.enums.StrategyType;
import com.chicu.neurotradebot.service.AiTradeSettingsService;
import com.chicu.neurotradebot.telegram.TelegramSender;
import com.chicu.neurotradebot.telegram.handler.CallbackHandler;
import com.chicu.neurotradebot.telegram.navigation.NavigationHistoryService;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.StrategyMenuBuilder;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.MlTpSlConfigMenuBuilder;
import com.chicu.neurotradebot.telegram.view.aimenu.strtegymenu.RsiConfigMenuBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class StrategyMenuCallbackHandler implements CallbackHandler {

    private final NavigationHistoryService navHistory;
    private final AiTradeSettingsService   settingsService;
    private final TelegramSender           sender;
    private final StrategyMenuBuilder      menu;
    private final MlTpSlConfigMenuBuilder  mlMenu;
    private final RsiConfigMenuBuilder     rsiMenu;
    // … другие ConfigMenuBuilder …

    @Override
    public boolean canHandle(Update upd) {
        if (!upd.hasCallbackQuery()) return false;
        String data = upd.getCallbackQuery().getData();
        return data.equals("ai_strategies")
                || data.equals("toggle_ml_tp_sl")
                || data.equals("config_ml_tp_sl")
                || data.startsWith("toggle_strat_")
                || data.startsWith("config_strat_");
    }

    @Override
    public void handle(Update upd) {
        CallbackQuery cq  = upd.getCallbackQuery();
        Long chatId       = cq.getMessage().getChatId();
        Integer msgId     = cq.getMessage().getMessageId();
        String data       = cq.getData();

        // === 1) Открыть главное меню стратегий ===
        if (data.equals("ai_strategies")) {
            // инициализируем историю ровно этим меню
            navHistory.clear(chatId);
            navHistory.push(chatId, "ai_strategies");
            sender.editMessage(chatId, msgId,
                    menu.title(), menu.markup(chatId));
            return;
        }

        // === 2) Переключить ML TP/SL ===
        if (data.equals("toggle_ml_tp_sl")) {
            settingsService.toggleMlTpSl(chatId);
            sender.editMessage(chatId, msgId,
                    menu.title(), menu.markup(chatId));
            return;
        }

        // === 3) Открыть меню настройки ML TP/SL ===
        if (data.equals("config_ml_tp_sl")) {
            // запоминаем, что назад → ai_strategies
            navHistory.push(chatId, "ai_strategies");
            mlMenu.buildOrEditMenu(chatId, msgId);
            return;
        }

        // === 4) Переключение галочки обычной стратегии ===
        if (data.startsWith("toggle_strat_")) {
            StrategyType st = StrategyType.valueOf(data.substring("toggle_strat_".length()));
            settingsService.toggleStrategy(chatId, st);
            sender.editMessage(chatId, msgId,
                    menu.title(), menu.markup(chatId));
            return;
        }

        // === 5) Открыть конфиг конкретной стратегии ===
        if (data.startsWith("config_strat_")) {
            StrategyType st = StrategyType.valueOf(data.substring("config_strat_".length()));
            // запоминаем точку возврата
            navHistory.push(chatId, "ai_strategies");

            if (st == StrategyType.RSI) {
                rsiMenu.buildOrEditMenu(chatId, msgId);
            } else {
                sender.editMessage(chatId, msgId,
                        "⚙️ Конфигурация для «" + st.getDisplayName() + "» пока недоступна",
                        menu.markup(chatId));
            }
        }
    }
}
